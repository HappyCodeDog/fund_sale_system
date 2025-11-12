package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for freezing funds
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeRequest {
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Account number
     */
    private String accountNumber;
    
    /**
     * Transaction serial number
     */
    private String transactionSerialNumber;
    
    /**
     * Amount to freeze
     */
    private BigDecimal amount;
    
    /**
     * Currency code
     */
    private String currencyCode;
    
    /**
     * Fee amount to freeze
     */
    private BigDecimal feeAmount;
    
    /**
     * Transaction type
     */
    private String transactionType;
    
    /**
     * Transaction description
     */
    private String description;
}

