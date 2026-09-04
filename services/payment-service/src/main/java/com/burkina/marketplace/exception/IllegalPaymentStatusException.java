package com.burkina.marketplace.exception;

public class IllegalPaymentStatusException extends RuntimeException {

    public IllegalPaymentStatusException(String message) {
        super(message);
    }
}
