package com.burkina.marketplace.exception;

public class SearchProductNotFoundException extends RuntimeException {

    public SearchProductNotFoundException(String message) {
        super(message);
    }
}
