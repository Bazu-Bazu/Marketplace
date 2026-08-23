package com.burkina.marketplace.exception;

public class ProductCategoryLimitExceededException extends RuntimeException {

    public ProductCategoryLimitExceededException(String message) {
        super(message);
    }
}
