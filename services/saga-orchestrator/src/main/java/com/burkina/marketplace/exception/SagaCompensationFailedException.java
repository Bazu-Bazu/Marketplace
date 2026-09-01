package com.burkina.marketplace.exception;

public class SagaCompensationFailedException extends RuntimeException {

    public SagaCompensationFailedException(String message) {
        super(message);
    }
}
