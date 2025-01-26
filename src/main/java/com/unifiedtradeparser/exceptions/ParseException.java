package com.unifiedtradeparser.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ParseException extends Exception {
    private final int lineNumber;
    private final String rawContent;

    public ParseException(String message, int lineNumber, String rawContent, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
        this.rawContent = rawContent;
    }
}