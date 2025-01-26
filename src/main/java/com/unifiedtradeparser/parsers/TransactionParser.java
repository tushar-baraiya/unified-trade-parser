package com.unifiedtradeparser.parsers;

import com.unifiedtradeparser.models.Transaction;
import com.unifiedtradeparser.exceptions.ParseException;

import java.io.File;
import java.util.List;

public interface TransactionParser<T extends Transaction> {
    List<T> parse(File file) throws ParseException;
    boolean supports(String fileContent);
}