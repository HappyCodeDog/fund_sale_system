package com.bank.fund.product.domain.model;

/**
 * Product status enumeration
 */
public enum ProductStatus {
    /**
     * Product is active and available
     */
    ACTIVE,
    
    /**
     * Product is suspended
     */
    SUSPENDED,
    
    /**
     * Product is closed/terminated
     */
    CLOSED,
    
    /**
     * Product is pending activation
     */
    PENDING
}

