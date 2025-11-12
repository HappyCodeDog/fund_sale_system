package com.bank.fund.trading.domain.model;

import com.bank.fund.common.domain.AggregateRoot;
import com.bank.fund.common.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subscription Transaction Aggregate Root
 * Represents a complete fund subscription transaction with its lifecycle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionTransaction implements AggregateRoot<String> {
    
    /**
     * Transaction serial number (unique identifier)
     */
    private String id;
    
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Account number
     */
    private String accountNumber;
    
    /**
     * Product code
     */
    private String productCode;
    
    /**
     * Subscription amount
     */
    private Money subscriptionAmount;
    
    /**
     * Fee rate
     */
    private java.math.BigDecimal feeRate;
    
    /**
     * Original fee (before discount)
     */
    private Money originalFee;
    
    /**
     * Discount amount (from coupon)
     */
    private Money discountAmount;
    
    /**
     * Final fee (after discount)
     */
    private Money finalFee;
    
    /**
     * Coupon ID (if used)
     */
    private String couponId;
    
    /**
     * Marketing system coupon usage ID
     */
    private String marketingUsageId;
    
    /**
     * Channel code (web, mobile, etc.)
     */
    private String channel;
    
    /**
     * Transaction status
     */
    private TransactionStatus status;
    
    /**
     * Core banking transaction ID
     */
    private String coreBankingTxnId;
    
    /**
     * Freeze ID (if freeze operation was used)
     */
    private String freezeId;
    
    /**
     * Saga state for compensation tracking
     */
    private SagaState sagaState;
    
    /**
     * Error code (if failed)
     */
    private String errorCode;
    
    /**
     * Error message (if failed)
     */
    private String errorMessage;
    
    /**
     * Whether this is first time subscription for this customer+product
     */
    private boolean firstTimeSubscription;
    
    /**
     * Request time
     */
    private LocalDateTime requestTime;
    
    /**
     * Completion time
     */
    private LocalDateTime completionTime;
    
    /**
     * Creation time
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update time
     */
    private LocalDateTime updatedAt;
    
    /**
     * Mark transaction as initialized
     */
    public void initialize() {
        this.status = TransactionStatus.INITIALIZED;
        this.sagaState = SagaState.INIT;
        this.requestTime = LocalDateTime.now();
    }
    
    /**
     * Mark transaction as validated
     */
    public void markValidated() {
        this.status = TransactionStatus.VALIDATED;
    }
    
    /**
     * Mark coupon as used
     */
    public void markCouponUsed(String marketingUsageId) {
        this.marketingUsageId = marketingUsageId;
        this.sagaState = SagaState.COUPON_USED;
    }
    
    /**
     * Mark accounting completed
     */
    public void markAccountingCompleted(String coreBankingTxnId) {
        this.coreBankingTxnId = coreBankingTxnId;
        this.sagaState = SagaState.ACCOUNTING_COMPLETED;
        this.status = TransactionStatus.ACCOUNTING_SUCCESS;
    }
    
    /**
     * Mark freeze completed
     */
    public void markFreezeCompleted(String freezeId) {
        this.freezeId = freezeId;
        this.sagaState = SagaState.FREEZE_COMPLETED;
        this.status = TransactionStatus.FREEZE_SUCCESS;
    }
    
    /**
     * Mark transaction as completed
     */
    public void markCompleted() {
        this.status = TransactionStatus.SUCCESS;
        this.sagaState = SagaState.COMPLETED;
        this.completionTime = LocalDateTime.now();
    }
    
    /**
     * Mark transaction as failed
     */
    public void markFailed(String errorCode, String errorMessage) {
        this.status = TransactionStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.completionTime = LocalDateTime.now();
    }
    
    /**
     * Check if coupon was used
     */
    public boolean hasCoupon() {
        return couponId != null && !couponId.isEmpty();
    }
    
    /**
     * Check if need to compensate coupon
     */
    public boolean needCouponCompensation() {
        return hasCoupon() && 
               (sagaState == SagaState.COUPON_USED || 
                sagaState == SagaState.ACCOUNTING_COMPLETED ||
                sagaState == SagaState.FREEZE_COMPLETED);
    }
    
    /**
     * Check if need to compensate accounting
     */
    public boolean needAccountingCompensation() {
        return sagaState == SagaState.ACCOUNTING_COMPLETED;
    }
    
    /**
     * Check if need to compensate freeze
     */
    public boolean needFreezeCompensation() {
        return sagaState == SagaState.FREEZE_COMPLETED;
    }
    
    /**
     * Calculate total deduction amount (subscription + fee)
     */
    public Money getTotalDeduction() {
        return subscriptionAmount.add(finalFee);
    }
}

