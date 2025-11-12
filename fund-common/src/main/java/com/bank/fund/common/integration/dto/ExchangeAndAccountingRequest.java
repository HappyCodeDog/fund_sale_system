package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for currency exchange + accounting operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeAndAccountingRequest {
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
     * Source currency code
     */
    private String sourceCurrency;
    
    /**
     * Target currency code
     */
    private String targetCurrency;
    
    /**
     * Amount in source currency
     */
    private BigDecimal sourceAmount;
    
    /**
     * Fee amount
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

