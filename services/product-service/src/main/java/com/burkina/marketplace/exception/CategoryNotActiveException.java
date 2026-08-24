package com.burkina.marketplace.exception;

public class CategoryNotActiveException extends RuntimeException {

    public CategoryNotActiveException(String message) {
        super(message);
    }
}
