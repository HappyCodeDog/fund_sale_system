package com.bank.fund.api;

import com.bank.fund.common.exception.BusinessException;
import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ExternalSystemException;
import com.bank.fund.common.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.warn("Validation exception: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle external system exceptions
     */
    @ExceptionHandler(ExternalSystemException.class)
    public ResponseEntity<ErrorResponse> handleExternalSystemException(ExternalSystemException e) {
        log.error("External system exception: errorCode={}, message={}", 
                  e.getErrorCode(), e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * Handle business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business exception: errorCode={}, message={}", 
                  e.getErrorCode(), e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle validation errors from @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Request validation failed: {}", errors);
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.INVALID_PARAMETER,
            "Request validation failed: " + errors.toString(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception occurred", e);
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.SYSTEM_ERROR,
            "An unexpected error occurred: " + e.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Error response DTO
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String errorMessage;
        private LocalDateTime timestamp;
    }
}

