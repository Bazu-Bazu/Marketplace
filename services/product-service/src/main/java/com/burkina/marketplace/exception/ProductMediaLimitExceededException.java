package com.burkina.marketplace.exception;

public class ProductMediaLimitExceededException extends RuntimeException {

    public ProductMediaLimitExceededException(String message) {
        super(message);
    }
}
