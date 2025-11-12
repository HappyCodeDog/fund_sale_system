package com.bank.fund.common.exception;

/**
 * Exception for external system integration failures
 */
public class ExternalSystemException extends BusinessException {
    
    public ExternalSystemException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ExternalSystemException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}

