package com.bank.fund.product.domain.model;

import com.bank.fund.common.domain.AggregateRoot;
import com.bank.fund.common.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fund Product Aggregate Root
 * Represents a fund product available for subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundProduct implements AggregateRoot<String> {
    
    /**
     * Product code (unique identifier)
     */
    private String id;
    
    /**
     * Product name
     */
    private String productName;
    
    /**
     * Product status
     */
    private ProductStatus productStatus;
    
    /**
     * Transaction status (subscription, redemption, etc.)
     */
    private TransactionStatus transactionStatus;
    
    /**
     * Product risk level (1-5)
     */
    private RiskLevel riskLevel;
    
    /**
     * Minimum initial subscription amount
     */
    private Money minInitialAmount;
    
    /**
     * Minimum additional subscription amount
     */
    private Money minAdditionalAmount;
    
    /**
     * Maximum subscription amount per transaction
     */
    private Money maxSubscriptionAmount;
    
    /**
     * Subscription amount unit (must be multiple of this)
     */
    private BigDecimal amountUnit;
    
    /**
     * Daily TA quota limit
     */
    private Money dailyTaQuota;
    
    /**
     * Allowed channels for this product
     */
    private String allowedChannels;
    
    /**
     * Product currency code
     */
    private String currencyCode;
    
    /**
     * Fee rate for subscription
     */
    private BigDecimal subscriptionFeeRate;
    
    /**
     * Creation time
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update time
     */
    private LocalDateTime updatedAt;
    
    /**
     * Check if product allows subscription
     */
    public boolean canSubscribe() {
        return productStatus == ProductStatus.ACTIVE 
            && transactionStatus.allowsSubscription();
    }
    
    /**
     * Check if channel is allowed for this product
     */
    public boolean isChannelAllowed(String channel) {
        if (allowedChannels == null || allowedChannels.isEmpty()) {
            return true; // No restriction
        }
        return allowedChannels.contains(channel);
    }
    
    /**
     * Validate subscription amount
     */
    public boolean isValidSubscriptionAmount(Money amount, boolean isInitial) {
        if (!amount.getCurrencyCode().equals(currencyCode)) {
            return false;
        }
        
        // Check minimum amount
        Money minAmount = isInitial ? minInitialAmount : minAdditionalAmount;
        if (amount.isLessThan(minAmount)) {
            return false;
        }
        
        // Check maximum amount
        if (maxSubscriptionAmount != null && amount.isGreaterThan(maxSubscriptionAmount)) {
            return false;
        }
        
        // Check amount unit
        if (amountUnit != null) {
            BigDecimal remainder = amount.getAmount().remainder(amountUnit);
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                return false;
            }
        }
        
        return true;
    }
}

