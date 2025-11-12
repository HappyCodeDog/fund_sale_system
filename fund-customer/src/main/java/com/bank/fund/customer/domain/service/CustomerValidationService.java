package com.bank.fund.customer.domain.service;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ValidationException;
import com.bank.fund.customer.domain.model.CustomerAccount;
import com.bank.fund.customer.domain.repository.CustomerAccountRepository;
import com.bank.fund.product.domain.model.RiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain service for customer validation
 */
@Service
@RequiredArgsConstructor
public class CustomerValidationService {
    
    private final CustomerAccountRepository customerRepository;
    
    /**
     * Validate customer account exists and is valid
     */
    public CustomerAccount validateCustomerAccount(String customerId) {
        CustomerAccount account = customerRepository.findById(customerId)
            .orElseThrow(() -> new ValidationException(
                ErrorCode.CUSTOMER_NOT_FOUND, 
                "Customer not found: " + customerId));
        
        if (!account.isValid()) {
            throw new ValidationException(
                ErrorCode.ACCOUNT_INVALID,
                String.format("Customer account %s is invalid. Status: %s, Suitability Expired: %s",
                    customerId, account.getAccountStatus(), account.isSuitabilityExpired()));
        }
        
        return account;
    }
    
    /**
     * Validate customer can buy product with given risk level
     */
    public void validateRiskMatch(CustomerAccount customer, RiskLevel productRiskLevel) {
        if (!customer.canBuyProduct(productRiskLevel)) {
            throw new ValidationException(
                ErrorCode.RISK_LEVEL_MISMATCH,
                String.format("Customer risk tolerance %d is lower than product risk level %d",
                    customer.getRiskTolerance().getLevel(), 
                    productRiskLevel.getLevel()));
        }
    }
}

