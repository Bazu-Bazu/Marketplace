package com.burkina.marketplace.exception;

public class MaxBankAccountLimitExceededException extends RuntimeException {

    public MaxBankAccountLimitExceededException(String message) {
        super(message);
    }
}
