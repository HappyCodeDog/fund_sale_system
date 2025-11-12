package com.bank.fund.trading.domain.model;

/**
 * Saga state for tracking compensation needs
 */
public enum SagaState {
    /**
     * Initial state
     */
    INIT,
    
    /**
     * Request saved to database
     */
    REQUEST_SAVED,
    
    /**
     * Coupon has been used (needs compensation if failed)
     */
    COUPON_USED,
    
    /**
     * Accounting completed (needs reversal if failed)
     */
    ACCOUNTING_COMPLETED,
    
    /**
     * Freeze completed (needs unfreeze if failed)
     */
    FREEZE_COMPLETED,
    
    /**
     * Status updated in database
     */
    STATUS_UPDATED,
    
    /**
     * All operations completed successfully
     */
    COMPLETED,
    
    /**
     * Compensation in progress
     */
    COMPENSATING,
    
    /**
     * Compensation completed
     */
    COMPENSATION_COMPLETED
}

