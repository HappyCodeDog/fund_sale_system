package com.bank.fund.trading.application;

import com.bank.fund.common.exception.BusinessException;
import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.integration.MarketingCouponService;
import com.bank.fund.common.integration.dto.*;
import com.bank.fund.common.money.Money;
import com.bank.fund.common.utils.SerialNumberGenerator;
import com.bank.fund.marketing.domain.model.CouponInfo;
import com.bank.fund.marketing.domain.model.CouponUsageRecord;
import com.bank.fund.marketing.domain.model.CouponUsageStatus;
import com.bank.fund.marketing.domain.model.FeeCalculation;
import com.bank.fund.marketing.domain.repository.CouponUsageRepository;
import com.bank.fund.marketing.domain.service.FeeCalculationService;
import com.bank.fund.trading.application.dto.SubscriptionRequest;
import com.bank.fund.trading.application.dto.SubscriptionResponse;
import com.bank.fund.trading.domain.model.ShareRecord;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.repository.ShareRecordRepository;
import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import com.bank.fund.trading.domain.service.AccountingService;
import com.bank.fund.trading.domain.service.SubscriptionValidationService;
import com.bank.fund.trading.domain.service.TransactionRollbackService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application service to orchestrate the complete subscription flow
 * Coordinates between multiple domain services and handles the saga
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionApplicationService {
    
    private final SubscriptionValidationService validationService;
    private final FeeCalculationService feeCalculationService;
    private final MarketingCouponService marketingCouponService;
    private final AccountingService accountingService;
    private final TransactionRollbackService rollbackService;
    private final SubscriptionTransactionRepository transactionRepository;
    private final ShareRecordRepository shareRecordRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final MeterRegistry meterRegistry;
    
    /**
     * Process subscription request
     * Complete flow: parse → validate → process → respond
     */
    @Transactional(rollbackFor = Exception.class)
    public SubscriptionResponse processSubscription(SubscriptionRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.info("[{}] Starting subscription process for customer: {}, product: {}, amount: {}", 
                 correlationId, request.getCustomerId(), request.getProductCode(), request.getAmount());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. Generate unique transaction serial number
            String serialNumber = SerialNumberGenerator.generate("SUB");
            log.info("[{}] Generated transaction serial number: {}", correlationId, serialNumber);
            
            // 2. Parse and create Money object
            Money subscriptionAmount = new Money(request.getAmount(), request.getCurrencyCode());
            
            // 3. Validation (multi-layer)
            SubscriptionValidationService.ValidationResult validationResult = 
                validationService.validate(
                    request.getCustomerId(),
                    request.getProductCode(),
                    request.getChannel(),
                    subscriptionAmount
                );
            log.info("[{}] Validation completed successfully", correlationId);
            
            // 4. Calculate fee (with or without coupon)
            FeeCalculation feeCalculation;
            CouponInfo couponInfo = null;
            
            if (request.getCouponId() != null && !request.getCouponId().isEmpty()) {
                // Trial calculate coupon discount
                couponInfo = trialCalculateCoupon(request, validationResult, correlationId);
                feeCalculation = feeCalculationService.calculateFeeWithCoupon(
                    subscriptionAmount,
                    validationResult.getProduct().getSubscriptionFeeRate(),
                    couponInfo
                );
            } else {
                feeCalculation = feeCalculationService.calculateFee(
                    subscriptionAmount,
                    validationResult.getProduct().getSubscriptionFeeRate()
                );
            }
            log.info("[{}] Fee calculation completed: originalFee={}, discount={}, finalFee={}", 
                     correlationId, feeCalculation.getOriginalFee(), 
                     feeCalculation.getDiscountAmount(), feeCalculation.getFinalFee());
            
            // 5. Create transaction aggregate
            SubscriptionTransaction transaction = createTransaction(
                serialNumber, request, subscriptionAmount, feeCalculation, 
                validationResult.isFirstTimeSubscription()
            );
            transaction.initialize();
            
            // 6. Save transaction (Saga: REQUEST_SAVED)
            transactionRepository.save(transaction);
            transaction.setSagaState(com.bank.fund.trading.domain.model.SagaState.REQUEST_SAVED);
            log.info("[{}] Transaction saved to database", correlationId);
            
            // 7. Create share record if first time (with 0 shares)
            if (validationResult.isFirstTimeSubscription()) {
                ShareRecord shareRecord = ShareRecord.createNew(
                    request.getCustomerId(),
                    request.getProductCode()
                );
                shareRecord.setId(UUID.randomUUID().toString());
                shareRecordRepository.save(shareRecord);
                log.info("[{}] Share record created for first time subscription", correlationId);
            }
            
            // 8. Use coupon if applicable (Saga Step 1)
            if (couponInfo != null) {
                String marketingUsageId = useCoupon(request, transaction, feeCalculation, correlationId);
                transaction.markCouponUsed(marketingUsageId);
                transactionRepository.update(transaction);
                
                // Save coupon usage record locally
                saveCouponUsageRecord(transaction, feeCalculation, correlationId);
            }
            
            // 9. Execute accounting operation (Saga Step 2)
            AccountingService.AccountingResult accountingResult = accountingService.executeAccounting(
                transaction,
                validationResult.getProduct().getCurrencyCode(),
                request.getCurrencyCode()  // Assuming this is account currency
            );
            
            if (accountingResult.isSuccess()) {
                if (accountingResult.getCoreBankingTxnId() != null) {
                    transaction.markAccountingCompleted(accountingResult.getCoreBankingTxnId());
                } else if (accountingResult.getFreezeId() != null) {
                    transaction.markFreezeCompleted(accountingResult.getFreezeId());
                }
                transactionRepository.update(transaction);
                log.info("[{}] Accounting completed: type={}, txnId={}, freezeId={}", 
                         correlationId, accountingResult.getType(), 
                         accountingResult.getCoreBankingTxnId(), accountingResult.getFreezeId());
            }
            
            // 10. Mark transaction as completed
            transaction.markCompleted();
            transactionRepository.update(transaction);
            log.info("[{}] Transaction completed successfully", correlationId);
            
            // 11. Record metrics
            long duration = System.currentTimeMillis() - startTime;
            recordMetrics("success", duration);
            
            // 12. Return success response
            return SubscriptionResponse.builder()
                .success(true)
                .transactionSerialNumber(serialNumber)
                .customerId(request.getCustomerId())
                .productCode(request.getProductCode())
                .subscriptionAmount(subscriptionAmount.getAmount())
                .finalFee(feeCalculation.getFinalFee().getAmount())
                .status(transaction.getStatus().name())
                .build();
            
        } catch (Exception e) {
            log.error("[{}] Subscription process failed", correlationId, e);
            
            // Handle compensation if transaction was created
            handleFailure(correlationId, e);
            
            recordMetrics("failed", System.currentTimeMillis() - startTime);
            
            return SubscriptionResponse.builder()
                .success(false)
                .errorCode(e instanceof BusinessException ? 
                    ((BusinessException) e).getErrorCode() : ErrorCode.SYSTEM_ERROR)
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    /**
     * Trial calculate coupon discount
     */
    @CircuitBreaker(name = "marketing")
    private CouponInfo trialCalculateCoupon(SubscriptionRequest request,
                                              SubscriptionValidationService.ValidationResult validationResult,
                                              String correlationId) {
        Money subscriptionAmount = new Money(request.getAmount(), request.getCurrencyCode());
        Money originalFee = subscriptionAmount.multiply(validationResult.getProduct().getSubscriptionFeeRate());
        
        CouponTrialRequest trialRequest = CouponTrialRequest.builder()
            .customerId(request.getCustomerId())
            .couponId(request.getCouponId())
            .productCode(request.getProductCode())
            .originalFee(originalFee.getAmount())
            .build();
        
        CouponTrialResponse trialResponse = marketingCouponService.trialCalculate(trialRequest);
        
        if (!trialResponse.isSuccess()) {
            throw new BusinessException(
                ErrorCode.COUPON_TRIAL_FAILED,
                "Coupon trial calculation failed: " + trialResponse.getErrorMessage()
            );
        }
        
        log.info("[{}] Coupon trial calculation succeeded: discountAmount={}", 
                 correlationId, trialResponse.getDiscountAmount());
        
        return new CouponInfo(
            trialResponse.getCouponId(),
            trialResponse.getCouponType(),
            trialResponse.getDiscountRate(),
            trialResponse.getDiscountAmount()
        );
    }
    
    /**
     * Use coupon
     */
    @CircuitBreaker(name = "marketing")
    private String useCoupon(SubscriptionRequest request,
                              SubscriptionTransaction transaction,
                              FeeCalculation feeCalculation,
                              String correlationId) {
        CouponUseRequest useRequest = CouponUseRequest.builder()
            .customerId(request.getCustomerId())
            .couponId(request.getCouponId())
            .transactionSerialNumber(transaction.getId())
            .productCode(request.getProductCode())
            .originalFee(feeCalculation.getOriginalFee().getAmount())
            .discountAmount(feeCalculation.getDiscountAmount().getAmount())
            .build();
        
        CouponUseResponse useResponse = marketingCouponService.useCoupon(useRequest);
        
        if (!useResponse.isSuccess()) {
            throw new BusinessException(
                ErrorCode.COUPON_USE_FAILED,
                "Coupon use failed: " + useResponse.getErrorMessage()
            );
        }
        
        log.info("[{}] Coupon used successfully: usageId={}", correlationId, useResponse.getUsageId());
        return useResponse.getUsageId();
    }
    
    /**
     * Save coupon usage record locally
     */
    private void saveCouponUsageRecord(SubscriptionTransaction transaction,
                                        FeeCalculation feeCalculation,
                                        String correlationId) {
        CouponUsageRecord record = CouponUsageRecord.builder()
            .id(UUID.randomUUID().toString())
            .transactionSerialNumber(transaction.getId())
            .customerId(transaction.getCustomerId())
            .couponId(transaction.getCouponId())
            .originalFee(feeCalculation.getOriginalFee().getAmount())
            .discountAmount(feeCalculation.getDiscountAmount().getAmount())
            .finalFee(feeCalculation.getFinalFee().getAmount())
            .status(CouponUsageStatus.USED)
            .usedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        couponUsageRepository.save(record);
        log.info("[{}] Coupon usage record saved locally", correlationId);
    }
    
    /**
     * Create transaction aggregate from request
     */
    private SubscriptionTransaction createTransaction(String serialNumber,
                                                        SubscriptionRequest request,
                                                        Money subscriptionAmount,
                                                        FeeCalculation feeCalculation,
                                                        boolean firstTime) {
        return SubscriptionTransaction.builder()
            .id(serialNumber)
            .customerId(request.getCustomerId())
            .accountNumber(request.getAccountNumber())
            .productCode(request.getProductCode())
            .subscriptionAmount(subscriptionAmount)
            .feeRate(feeCalculation.getFeeRate())
            .originalFee(feeCalculation.getOriginalFee())
            .discountAmount(feeCalculation.getDiscountAmount())
            .finalFee(feeCalculation.getFinalFee())
            .couponId(request.getCouponId())
            .channel(request.getChannel())
            .firstTimeSubscription(firstTime)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * Handle failure and trigger compensation
     */
    private void handleFailure(String correlationId, Exception e) {
        log.error("[{}] Handling failure, triggering compensation if needed", correlationId);
        // In a real implementation, we would retrieve the partially completed transaction
        // and trigger async compensation. For now, we just log.
    }
    
    /**
     * Record metrics
     */
    private void recordMetrics(String result, long duration) {
        meterRegistry.counter("subscription.request", "result", result).increment();
        meterRegistry.timer("subscription.duration", "result", result)
            .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}

