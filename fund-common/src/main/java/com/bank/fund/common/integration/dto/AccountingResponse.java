package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for core banking accounting operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingResponse {
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Core banking transaction ID
     */
    private String coreBankingTxnId;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

