package com.unifiedtradeparser.config;

import java.io.Reader;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CSVConfig {
    private char separator = ',';
    private char quoteChar = '"';
    private char escapeChar = '\\';
    private boolean ignoreLeadingWhiteSpace = true;
    private boolean ignoreQuotations = false;
    private boolean multilineEnabled = true;

    public CSVReader createReader(Reader reader) {
        return new CSVReaderBuilder(reader)
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(separator)
                        .withQuoteChar(quoteChar)
                        .withEscapeChar(escapeChar)
                        .withIgnoreLeadingWhiteSpace(ignoreLeadingWhiteSpace)
                        .withIgnoreQuotations(ignoreQuotations)
                        .build())
                .build();
    }
}