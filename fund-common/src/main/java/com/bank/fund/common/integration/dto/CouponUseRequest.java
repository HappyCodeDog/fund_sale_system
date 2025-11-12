package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for consuming a coupon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseRequest {
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Coupon ID
     */
    private String couponId;
    
    /**
     * Transaction serial number
     */
    private String transactionSerialNumber;
    
    /**
     * Product code
     */
    private String productCode;
    
    /**
     * Original fee amount
     */
    private BigDecimal originalFee;
    
    /**
     * Discount amount
     */
    private BigDecimal discountAmount;
}

