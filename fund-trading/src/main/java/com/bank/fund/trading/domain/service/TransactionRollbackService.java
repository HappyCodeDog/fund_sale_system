package com.bank.fund.trading.domain.service;

import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.common.integration.MarketingCouponService;
import com.bank.fund.common.integration.dto.AccountingResponse;
import com.bank.fund.common.integration.dto.CouponReturnRequest;
import com.bank.fund.common.integration.dto.CouponReturnResponse;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Domain service for async compensation (rollback) operations
 * Handles: 冲正 (reversal), 解冻 (unfreeze), 还券 (coupon return)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRollbackService {
    
    private final CoreBankingService coreBankingService;
    private final MarketingCouponService marketingCouponService;
    
    /**
     * Execute full compensation based on transaction saga state
     */
    @Async
    public CompletableFuture<CompensationResult> compensate(SubscriptionTransaction transaction) {
        log.info("Starting compensation for transaction: {}, sagaState: {}", 
                 transaction.getId(), transaction.getSagaState());
        
        CompensationResult result = new CompensationResult();
        
        try {
            // Compensate in reverse order of execution
            
            // 1. Compensate accounting/freeze if needed
            if (transaction.needAccountingCompensation()) {
                log.info("Compensating accounting for transaction: {}", transaction.getId());
                boolean reversalSuccess = compensateAccounting(transaction);
                result.setAccountingCompensated(reversalSuccess);
                if (!reversalSuccess) {
                    log.error("Accounting compensation failed for transaction: {}", transaction.getId());
                    // Continue to try coupon compensation even if accounting fails
                }
            } else if (transaction.needFreezeCompensation()) {
                log.info("Compensating freeze for transaction: {}", transaction.getId());
                boolean unfreezeSuccess = compensateFreeze(transaction);
                result.setFreezeCompensated(unfreezeSuccess);
                if (!unfreezeSuccess) {
                    log.error("Freeze compensation failed for transaction: {}", transaction.getId());
                }
            }
            
            // 2. Compensate coupon if needed
            if (transaction.needCouponCompensation()) {
                log.info("Compensating coupon for transaction: {}", transaction.getId());
                boolean couponSuccess = compensateCoupon(transaction);
                result.setCouponCompensated(couponSuccess);
                if (!couponSuccess) {
                    log.error("Coupon compensation failed for transaction: {}", transaction.getId());
                }
            }
            
            result.setSuccess(result.isAllCompensated());
            log.info("Compensation completed for transaction: {}, result: {}", 
                     transaction.getId(), result);
            
        } catch (Exception e) {
            log.error("Unexpected error during compensation for transaction: {}", 
                      transaction.getId(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return CompletableFuture.completedFuture(result);
    }
    
    /**
     * Compensate accounting operation (reversal/冲正)
     */
    @CircuitBreaker(name = "coreBank", fallbackMethod = "compensateAccountingFallback")
    private boolean compensateAccounting(SubscriptionTransaction transaction) {
        try {
            AccountingResponse response = coreBankingService.reversal(
                transaction.getCoreBankingTxnId(),
                transaction.getId()
            );
            
            if (response.isSuccess()) {
                log.info("Accounting reversal succeeded for transaction: {}", transaction.getId());
                return true;
            } else {
                log.error("Accounting reversal failed for transaction {}: {}", 
                         transaction.getId(), response.getErrorMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception during accounting reversal for transaction: {}", 
                      transaction.getId(), e);
            return false;
        }
    }
    
    /**
     * Compensate freeze operation (unfreeze/解冻)
     */
    @CircuitBreaker(name = "coreBank", fallbackMethod = "compensateFreezeFallback")
    private boolean compensateFreeze(SubscriptionTransaction transaction) {
        try {
            AccountingResponse response = coreBankingService.unfreeze(
                transaction.getFreezeId(),
                transaction.getId()
            );
            
            if (response.isSuccess()) {
                log.info("Unfreeze succeeded for transaction: {}", transaction.getId());
                return true;
            } else {
                log.error("Unfreeze failed for transaction {}: {}", 
                         transaction.getId(), response.getErrorMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception during unfreeze for transaction: {}", 
                      transaction.getId(), e);
            return false;
        }
    }
    
    /**
     * Compensate coupon (return/还券)
     */
    @CircuitBreaker(name = "marketing", fallbackMethod = "compensateCouponFallback")
    private boolean compensateCoupon(SubscriptionTransaction transaction) {
        try {
            CouponReturnRequest request = CouponReturnRequest.builder()
                .customerId(transaction.getCustomerId())
                .couponId(transaction.getCouponId())
                .transactionSerialNumber(transaction.getId())
                .usageId(transaction.getMarketingUsageId())
                .reason("Transaction failed - compensation")
                .build();
            
            CouponReturnResponse response = marketingCouponService.returnCoupon(request);
            
            if (response.isSuccess()) {
                log.info("Coupon return succeeded for transaction: {}", transaction.getId());
                return true;
            } else {
                log.error("Coupon return failed for transaction {}: {}", 
                         transaction.getId(), response.getErrorMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception during coupon return for transaction: {}", 
                      transaction.getId(), e);
            return false;
        }
    }
    
    // Fallback methods for circuit breaker
    
    private boolean compensateAccountingFallback(SubscriptionTransaction transaction, Exception e) {
        log.error("Circuit breaker open for accounting reversal, transaction: {}", 
                  transaction.getId(), e);
        return false;
    }
    
    private boolean compensateFreezeFallback(SubscriptionTransaction transaction, Exception e) {
        log.error("Circuit breaker open for unfreeze, transaction: {}", 
                  transaction.getId(), e);
        return false;
    }
    
    private boolean compensateCouponFallback(SubscriptionTransaction transaction, Exception e) {
        log.error("Circuit breaker open for coupon return, transaction: {}", 
                  transaction.getId(), e);
        return false;
    }
    
    /**
     * Result of compensation operations
     */
    @lombok.Data
    public static class CompensationResult {
        private boolean success;
        private boolean accountingCompensated;
        private boolean freezeCompensated;
        private boolean couponCompensated;
        private String errorMessage;
        
        public boolean isAllCompensated() {
            // Returns true if all needed compensations succeeded
            // (we don't fail if a compensation wasn't needed)
            return success;
        }
    }
}

