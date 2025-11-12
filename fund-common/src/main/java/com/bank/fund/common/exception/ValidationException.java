package com.bank.fund.common.exception;

/**
 * Validation exception for business rule violations
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}

