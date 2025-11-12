package com.bank.fund.trading.infrastructure.persistence.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistent object for subscription transaction table
 */
@Data
public class SubscriptionTransactionPO {
    private String serialNumber;
    private String customerId;
    private String accountNumber;
    private String productCode;
    private BigDecimal subscriptionAmount;
    private String currencyCode;
    private BigDecimal feeRate;
    private BigDecimal originalFee;
    private BigDecimal discountAmount;
    private BigDecimal finalFee;
    private String couponId;
    private String marketingUsageId;
    private String channel;
    private String status;
    private String coreBankingTxnId;
    private String freezeId;
    private String sagaState;
    private String errorCode;
    private String errorMessage;
    private String firstTimeSubscription;
    private LocalDateTime requestTime;
    private LocalDateTime completionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

