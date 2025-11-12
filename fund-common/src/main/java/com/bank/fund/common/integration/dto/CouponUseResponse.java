package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for coupon usage
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseResponse {
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Coupon usage ID from marketing system
     */
    private String usageId;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

