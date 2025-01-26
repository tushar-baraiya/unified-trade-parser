package com.unifiedtradeparser.parsers;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.unifiedtradeparser.models.Transaction;

public class FidelityTransactionParserTest {

    @Test
    public void testParseValidFile() throws Exception {
        File sampleFile = new File("src/test/resources/fidelity_sample_trades.csv");
        FidelityTransactionParser parser = new FidelityTransactionParser();

        List<Transaction> transactions = parser.parse(sampleFile);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

    }

    @Test
    public void testParseWithMissingFields() throws Exception {
        File sampleFile = new File("src/test/resources/fidelity_missing_fields.csv");
        FidelityTransactionParser parser = new FidelityTransactionParser();

        List<Transaction> transactions = parser.parse(sampleFile);

        assertNotNull(transactions);
        assertEquals(0.0, transactions.get(0).getCommission()); // Default value for missing commission
    }
}
