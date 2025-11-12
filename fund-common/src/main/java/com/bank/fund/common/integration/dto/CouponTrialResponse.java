package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for coupon trial calculation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTrialResponse {
    /**
     * Success flag
     */
    private boolean success;
    
    /**
     * Coupon ID
     */
    private String couponId;
    
    /**
     * Coupon type
     */
    private String couponType;
    
    /**
     * Discount rate (if percentage-based)
     */
    private BigDecimal discountRate;
    
    /**
     * Discount amount (if fixed-amount or calculated)
     */
    private BigDecimal discountAmount;
    
    /**
     * Final fee after discount
     */
    private BigDecimal finalFee;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
}

