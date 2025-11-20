package com.khanh.commerce.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class DuplicateException extends IllegalArgumentException {
    private final List<String> errors;

    public DuplicateException(List<String> errors) {
        super("Duplicate data found");
        this.errors = errors;
    }
}
