package com.bank.fund.trading.domain.service;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ValidationException;
import com.bank.fund.common.money.Money;
import com.bank.fund.customer.domain.model.CustomerAccount;
import com.bank.fund.customer.domain.service.CustomerValidationService;
import com.bank.fund.product.domain.model.FundProduct;
import com.bank.fund.product.domain.service.ProductValidationService;
import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Domain service for comprehensive subscription validation
 * Implements multi-layer validation: product, account, amount/quota
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionValidationService {
    
    private final ProductValidationService productValidationService;
    private final CustomerValidationService customerValidationService;
    private final SubscriptionTransactionRepository transactionRepository;
    
    // Daily quota tracking (in-memory, in production should use Redis or database)
    private final ConcurrentHashMap<String, BigDecimal> dailyQuotaUsage = new ConcurrentHashMap<>();
    
    /**
     * Perform complete validation for subscription request
     */
    public ValidationResult validate(String customerId, String productCode, 
                                      String channel, Money amount) {
        log.info("Starting validation for subscription: customer={}, product={}, channel={}, amount={}", 
                 customerId, productCode, channel, amount);
        
        // 2.1 Product validation
        FundProduct product = productValidationService.validateForSubscription(productCode, channel);
        log.info("Product validation passed for product: {}", productCode);
        
        // 2.2 Customer and account validation
        CustomerAccount customer = customerValidationService.validateCustomerAccount(customerId);
        log.info("Customer validation passed for customer: {}", customerId);
        
        // Risk level matching
        customerValidationService.validateRiskMatch(customer, product.getRiskLevel());
        log.info("Risk level matching passed: customer={}, product={}", 
                 customer.getRiskTolerance().getLevel(), 
                 product.getRiskLevel().getLevel());
        
        // 2.3 Amount validation
        boolean isFirstTime = !transactionRepository.hasExistingSubscription(customerId, productCode);
        productValidationService.validateSubscriptionAmount(product, amount, isFirstTime);
        log.info("Amount validation passed: amount={}, firstTime={}", amount, isFirstTime);
        
        // Daily TA quota validation
        validateDailyQuota(product, amount);
        log.info("Daily quota validation passed for product: {}", productCode);
        
        return ValidationResult.builder()
            .product(product)
            .customer(customer)
            .firstTimeSubscription(isFirstTime)
            .build();
    }
    
    /**
     * Validate daily TA quota
     */
    private void validateDailyQuota(FundProduct product, Money amount) {
        if (product.getDailyTaQuota() == null) {
            return; // No quota limit
        }
        
        String quotaKey = product.getId() + "_" + LocalDate.now();
        BigDecimal currentUsage = dailyQuotaUsage.getOrDefault(quotaKey, BigDecimal.ZERO);
        BigDecimal newUsage = currentUsage.add(amount.getAmount());
        
        if (newUsage.compareTo(product.getDailyTaQuota().getAmount()) > 0) {
            throw new ValidationException(
                ErrorCode.QUOTA_EXCEEDED,
                String.format("Daily TA quota exceeded for product %s. Current: %s, Requested: %s, Limit: %s",
                    product.getId(), currentUsage, amount.getAmount(), 
                    product.getDailyTaQuota().getAmount()));
        }
        
        dailyQuotaUsage.put(quotaKey, newUsage);
    }
    
    /**
     * Validation result containing validated entities
     */
    @lombok.Value
    @lombok.Builder
    public static class ValidationResult {
        FundProduct product;
        CustomerAccount customer;
        boolean firstTimeSubscription;
    }
}

