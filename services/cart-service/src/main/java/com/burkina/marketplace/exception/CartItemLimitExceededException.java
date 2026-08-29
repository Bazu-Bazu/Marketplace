package com.burkina.marketplace.exception;

public class CartItemLimitExceededException extends RuntimeException {

    public CartItemLimitExceededException(String message) {
        super(message);
    }
}
