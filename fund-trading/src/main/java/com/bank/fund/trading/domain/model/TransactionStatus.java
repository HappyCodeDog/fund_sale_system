package com.bank.fund.trading.domain.model;

/**
 * Transaction status enumeration
 */
public enum TransactionStatus {
    /**
     * Transaction initialized
     */
    INITIALIZED,
    
    /**
     * Validation completed
     */
    VALIDATED,
    
    /**
     * Accounting operation succeeded
     */
    ACCOUNTING_SUCCESS,
    
    /**
     * Freeze operation succeeded
     */
    FREEZE_SUCCESS,
    
    /**
     * Transaction completed successfully
     */
    SUCCESS,
    
    /**
     * Transaction failed
     */
    FAILED,
    
    /**
     * Transaction under compensation
     */
    COMPENSATING
}

