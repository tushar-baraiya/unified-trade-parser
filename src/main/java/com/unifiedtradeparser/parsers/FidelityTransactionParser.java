package com.unifiedtradeparser.parsers;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.unifiedtradeparser.config.CSVConfig;
import com.unifiedtradeparser.exceptions.ParseException;
import com.unifiedtradeparser.models.StockTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class FidelityTransactionParser implements TransactionParser<StockTransaction> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Set<String> REQUIRED_HEADERS = ImmutableSet.of(
            "Run Date", "Account", "Action", "Symbol", "Amount"
    );

    private final CSVConfig csvConfig;

    public FidelityTransactionParser() {
        this.csvConfig = new CSVConfig();
    }

    public FidelityTransactionParser(CSVConfig csvConfig) {
        this.csvConfig = csvConfig;
    }

    @Override
    public List<StockTransaction> parse(File file) throws ParseException {
        try (InputStream inputStream = new FileInputStream(file);
             Reader reader = new InputStreamReader(removeUTF8BOM(inputStream), StandardCharsets.UTF_8);
             CSVReader csvReader = csvConfig.createReader(reader)) {

            String[] headers = findHeaders(csvReader);
            if (headers == null) {
                throw new ParseException("No valid headers found", 0, "", null);
            }

            validateHeaders(headers);
            return parseRows(csvReader, headers);

        } catch (IOException e) {
            log.error("Failed to read file: {}", file.getName(), e);
            throw new ParseException("Failed to read file: " + e.getMessage(), 0, "", e);
        }
    }

    @Override
    public boolean supports(String fileContent) {
        return fileContent.contains("Individual - TOD") || 
               fileContent.contains("YOU BOUGHT CLOSING TRANSACTION");
    }

    private InputStream removeUTF8BOM(InputStream is) throws IOException {
        PushbackInputStream pushback = new PushbackInputStream(is, 3);
        byte[] bom = new byte[3];
        if (pushback.read(bom) != -1) {
            if (!(bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF)) {
                pushback.unread(bom);
            }
        }
        return pushback;
    }

    private String[] findHeaders(CSVReader reader) throws IOException {
        String[] row;
        while ((row = reader.readNext()) != null) {
            if (isEmptyRow(row)) {
                log.debug("Skipping empty row");
                continue;
            }
            
            if (containsRequiredHeaders(row)) {
                log.debug("Found headers: {}", Arrays.toString(row));
                return normalizeHeaders(row);
            }
        }
        return null;
    }

    private boolean isEmptyRow(String[] row) {
        return Arrays.stream(row).allMatch(StringUtils::isBlank);
    }

    private boolean containsRequiredHeaders(String[] row) {
        Set<String> normalizedRowHeaders = Arrays.stream(row)
                .filter(StringUtils::isNotBlank)
                .map(this::normalizeHeader)
                .collect(ImmutableSet.toImmutableSet());
        
        return normalizedRowHeaders.containsAll(REQUIRED_HEADERS);
    }

    private String[] normalizeHeaders(String[] headers) {
        return Arrays.stream(headers)
                .map(this::normalizeHeader)
                .toArray(String[]::new);
    }

    private String normalizeHeader(String header) {
        return Strings.nullToEmpty(header).trim().toLowerCase()
                .replace(" ", "_")
                .replace("/", "_")
                .replace("(", "")
                .replace(")", "");
    }

    private void validateHeaders(String[] headers) throws ParseException {
        Set<String> normalizedHeaders = ImmutableSet.copyOf(headers);
        Set<String> missingHeaders = new HashSet<>(REQUIRED_HEADERS);
        missingHeaders.removeAll(normalizedHeaders);

        if (!missingHeaders.isEmpty()) {
            String message = String.format("Missing required headers: %s", String.join(", ", missingHeaders));
            log.error(message);
            throw new ParseException(message, 0, String.join(",", headers), null);
        }
    }

    private List<StockTransaction> parseRows(CSVReader reader, String[] headers) throws ParseException {
        List<StockTransaction> transactions = new ArrayList<>();
        String[] row;
        int lineNumber = 1;

        try {
            while ((row = reader.readNext()) != null) {
                lineNumber++;
                if (isEmptyRow(row)) {
                    log.debug("Skipping empty row at line {}", lineNumber);
                    continue;
                }

                try {
                    transactions.add(parseTransaction(row, headers));
                } catch (Exception e) {
                    log.warn("Error parsing row {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (IOException | CsvException e) {
            log.error("Error reading CSV at line {}", lineNumber, e);
            throw new ParseException("Error reading CSV", lineNumber, "", e);
        }

        return transactions;
    }

    private StockTransaction parseTransaction(String[] row, String[] headers) {
        Map<String, String> rowData = new HashMap<>();
        for (int i = 0; i < headers.length && i < row.length; i++) {
            rowData.put(headers[i], StringUtils.trimToEmpty(row[i]));
        }

        return StockTransaction.builder()
                .runDate(parseDate(rowData.get("run_date")))
                .account(rowData.get("account"))
                .action(rowData.get("action"))
                .symbol(rowData.get("symbol"))
                .description(rowData.get("description"))
                .type("STOCK")
                .exchangeQuantity(parseBigDecimal(rowData.get("exchange_quantity")))
                .exchangeCurrency(rowData.get("exchange_currency"))
                .quantity(parseBigDecimal(rowData.get("quantity")))
                .currency(rowData.get("currency"))
                .price(parseBigDecimal(rowData.get("price")))
                .exchangeRate(parseBigDecimal(rowData.get("exchange_rate")))
                .commission(parseBigDecimal(rowData.get("commission")))
                .fees(parseBigDecimal(rowData.get("fees")))
                .accruedInterest(parseBigDecimal(rowData.get("accrued_interest")))
                .amount(parseBigDecimal(rowData.get("amount")))
                .settlementDate(parseDate(rowData.get("settlement_date")))
                .build();
    }

    private LocalDate parseDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            log.debug("Failed to parse date: {}", value);
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            log.debug("Failed to parse number: {}", value);
            return BigDecimal.ZERO;
        }
    }
}