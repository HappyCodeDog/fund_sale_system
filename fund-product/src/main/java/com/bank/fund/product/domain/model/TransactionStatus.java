package com.bank.fund.product.domain.model;

/**
 * Transaction status - controls which operations are allowed
 */
public enum TransactionStatus {
    /**
     * All transactions allowed
     */
    ALL,
    
    /**
     * Only subscription allowed
     */
    SUBSCRIPTION_ONLY,
    
    /**
     * Only redemption allowed
     */
    REDEMPTION_ONLY,
    
    /**
     * No transactions allowed
     */
    NONE;
    
    public boolean allowsSubscription() {
        return this == ALL || this == SUBSCRIPTION_ONLY;
    }
    
    public boolean allowsRedemption() {
        return this == ALL || this == REDEMPTION_ONLY;
    }
}

