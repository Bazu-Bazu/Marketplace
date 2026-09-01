package com.burkina.marketplace.exception;

public class CartServiceUnavailableException extends RuntimeException {

    public CartServiceUnavailableException(String message) {
        super(message);
    }
}
