package com.bank.fund.trading.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for fund subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Transaction serial number
     */
    private String transactionSerialNumber;
    
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Product code
     */
    private String productCode;
    
    /**
     * Subscription amount
     */
    private BigDecimal subscriptionAmount;
    
    /**
     * Final fee after discount
     */
    private BigDecimal finalFee;
    
    /**
     * Transaction status
     */
    private String status;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

