package com.burkina.marketplace.exception;

public class MaxPhoneLimitExceededException extends RuntimeException {

    public MaxPhoneLimitExceededException(String message) {
        super(message);
    }
}
