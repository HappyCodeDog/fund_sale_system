package com.bank.fund.marketing.domain.model;

import com.bank.fund.common.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Coupon usage record entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsageRecord implements Entity<String> {
    
    /**
     * Usage record ID
     */
    private String id;
    
    /**
     * Transaction serial number
     */
    private String transactionSerialNumber;
    
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Coupon ID
     */
    private String couponId;
    
    /**
     * Original fee before discount
     */
    private BigDecimal originalFee;
    
    /**
     * Discount amount
     */
    private BigDecimal discountAmount;
    
    /**
     * Final fee after discount
     */
    private BigDecimal finalFee;
    
    /**
     * Usage status
     */
    private CouponUsageStatus status;
    
    /**
     * Usage time
     */
    private LocalDateTime usedAt;
    
    /**
     * Return time (if returned)
     */
    private LocalDateTime returnedAt;
    
    /**
     * Creation time
     */
    private LocalDateTime createdAt;
}

