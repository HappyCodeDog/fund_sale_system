package com.bank.fund.marketing.infrastructure.persistence.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistent object for coupon usage record table
 */
@Data
public class CouponUsageRecordPO {
    private String id;
    private String transactionSerialNumber;
    private String customerId;
    private String couponId;
    private BigDecimal originalFee;
    private BigDecimal discountAmount;
    private BigDecimal finalFee;
    private String status;
    private LocalDateTime usedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime createdAt;
}

