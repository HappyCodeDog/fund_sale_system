package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for coupon trial calculation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTrialRequest {
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Coupon ID
     */
    private String couponId;
    
    /**
     * Product code
     */
    private String productCode;
    
    /**
     * Original fee amount
     */
    private BigDecimal originalFee;
}

