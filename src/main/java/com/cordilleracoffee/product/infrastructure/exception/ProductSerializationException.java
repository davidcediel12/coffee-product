package com.cordilleracoffee.product.infrastructure.exception;

public class ProductSerializationException extends RuntimeException {
    public ProductSerializationException(String message) {
        super(message);
    }

    public ProductSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
