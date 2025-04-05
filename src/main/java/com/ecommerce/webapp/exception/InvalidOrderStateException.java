package com.ecommerce.webapp.exception;

import lombok.Data;

@Data
public class InvalidOrderStateException extends RuntimeException {
    private String code;
    public InvalidOrderStateException(String code, String message) {
        super(message);
        this.code = code;

    }
}
