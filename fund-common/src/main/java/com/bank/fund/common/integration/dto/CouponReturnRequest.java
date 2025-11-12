package com.bank.fund.common.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for returning/refunding a coupon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponReturnRequest {
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
     * Coupon usage ID from marketing system
     */
    private String usageId;
    
    /**
     * Reason for return
     */
    private String reason;
}

