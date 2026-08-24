package com.burkina.marketplace.exception;

public class ProductCategoryAlreadyExistsException extends RuntimeException {

    public ProductCategoryAlreadyExistsException(String message) {
        super(message);
    }
}
