package com.burkina.marketplace.exception;

public class MaxEmailLimitExceededException extends RuntimeException {

    public MaxEmailLimitExceededException(String message) {
        super(message);
    }
}
