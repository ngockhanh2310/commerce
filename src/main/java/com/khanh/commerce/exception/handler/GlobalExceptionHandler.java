package com.khanh.commerce.exception.handler;


import com.khanh.commerce.dto.response.ApiResponse;
import com.khanh.commerce.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return ApiResponse.builder()
                .message(ex.getMessage())
                .build();
    }

    // 2. Handler 400 (Trùng lặp)
    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<String>> handleDuplicate(DuplicateException ex) {
        return ApiResponse.<List<String>>builder()
                .message("Error")
                .data(ex.getErrors())
                .build();
    }

    // 3. Handler 400 (Lỗi Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.builder()
                .message("Validation failed")
                .data(errors)
                .build();
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Object> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ApiResponse.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleFileStorageException(FileStorageException ex) {
        return ApiResponse.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(CustomAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleCustomAccessException(CustomAccessException ex) {
        return ApiResponse.builder()
                .message(ex.getMessage())
                .build();
    }
}
