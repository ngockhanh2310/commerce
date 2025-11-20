package com.khanh.commerce.exception;

import org.springframework.security.access.AccessDeniedException;

public class CustomAccessException extends AccessDeniedException {
    public CustomAccessException(String message) {
        super(message);
    }
}
