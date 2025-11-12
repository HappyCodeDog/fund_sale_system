package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for coupon return
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponReturnResponse {
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

