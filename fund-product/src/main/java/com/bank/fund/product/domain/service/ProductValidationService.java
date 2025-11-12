package com.bank.fund.product.domain.service;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ValidationException;
import com.bank.fund.common.money.Money;
import com.bank.fund.product.domain.model.FundProduct;
import com.bank.fund.product.domain.repository.FundProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain service for product validation
 */
@Service
@RequiredArgsConstructor
public class ProductValidationService {
    
    private final FundProductRepository productRepository;
    
    /**
     * Validate product for subscription
     */
    public FundProduct validateForSubscription(String productCode, String channel) {
        FundProduct product = productRepository.findById(productCode)
            .orElseThrow(() -> new ValidationException(
                ErrorCode.PRODUCT_NOT_FOUND, 
                "Product not found: " + productCode));
        
        if (!product.canSubscribe()) {
            throw new ValidationException(
                ErrorCode.PRODUCT_STATUS_INVALID,
                String.format("Product %s cannot be subscribed. Status: %s, Transaction Status: %s",
                    productCode, product.getProductStatus(), product.getTransactionStatus()));
        }
        
        if (!product.isChannelAllowed(channel)) {
            throw new ValidationException(
                ErrorCode.CHANNEL_NOT_ALLOWED,
                String.format("Channel %s is not allowed for product %s", channel, productCode));
        }
        
        return product;
    }
    
    /**
     * Validate subscription amount
     */
    public void validateSubscriptionAmount(FundProduct product, Money amount, boolean isInitial) {
        if (!product.isValidSubscriptionAmount(amount, isInitial)) {
            String minAmount = isInitial ? 
                product.getMinInitialAmount().getAmount().toString() : 
                product.getMinAdditionalAmount().getAmount().toString();
            
            if (amount.isLessThan(isInitial ? product.getMinInitialAmount() : product.getMinAdditionalAmount())) {
                throw new ValidationException(
                    ErrorCode.AMOUNT_TOO_LOW,
                    String.format("Subscription amount %s is below minimum %s for product %s",
                        amount.getAmount(), minAmount, product.getId()));
            }
            
            if (product.getMaxSubscriptionAmount() != null && amount.isGreaterThan(product.getMaxSubscriptionAmount())) {
                throw new ValidationException(
                    ErrorCode.AMOUNT_TOO_HIGH,
                    String.format("Subscription amount %s exceeds maximum %s for product %s",
                        amount.getAmount(), product.getMaxSubscriptionAmount().getAmount(), product.getId()));
            }
            
            throw new ValidationException(
                ErrorCode.AMOUNT_INVALID_UNIT,
                String.format("Subscription amount %s is not a valid unit for product %s",
                    amount.getAmount(), product.getId()));
        }
    }
}

