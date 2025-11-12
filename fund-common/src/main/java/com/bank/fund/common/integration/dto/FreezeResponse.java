package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for freeze operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeResponse {
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Freeze ID
     */
    private String freezeId;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

