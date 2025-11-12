package com.bank.fund.product.infrastructure.persistence.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistent object for fund product table
 */
@Data
public class FundProductPO {
    private String productCode;
    private String productName;
    private String productStatus;
    private String transactionStatus;
    private Integer riskLevel;
    private BigDecimal minInitialAmount;
    private BigDecimal minAdditionalAmount;
    private BigDecimal maxSubscriptionAmount;
    private BigDecimal amountUnit;
    private BigDecimal dailyTaQuota;
    private String allowedChannels;
    private String currencyCode;
    private BigDecimal subscriptionFeeRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

