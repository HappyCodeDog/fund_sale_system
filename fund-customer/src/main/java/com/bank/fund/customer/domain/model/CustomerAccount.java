package com.bank.fund.customer.domain.model;

import com.bank.fund.common.domain.AggregateRoot;
import com.bank.fund.product.domain.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer Account Aggregate Root
 * Represents a customer's account with risk profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccount implements AggregateRoot<String> {
    
    /**
     * Customer ID (unique identifier)
     */
    private String id;
    
    /**
     * Customer name
     */
    private String customerName;
    
    /**
     * Customer type (INDIVIDUAL, CORPORATE, etc.)
     */
    private CustomerType customerType;
    
    /**
     * Account number
     */
    private String accountNumber;
    
    /**
     * Account status
     */
    private AccountStatus accountStatus;
    
    /**
     * Risk tolerance level (1-5)
     */
    private RiskLevel riskTolerance;
    
    /**
     * Investment suitability assessment date
     */
    private LocalDateTime suitabilityAssessmentDate;
    
    /**
     * Whether suitability assessment is expired
     */
    private boolean suitabilityExpired;
    
    /**
     * ID type (ID_CARD, PASSPORT, etc.)
     */
    private String idType;
    
    /**
     * ID number
     */
    private String idNumber;
    
    /**
     * Phone number
     */
    private String phoneNumber;
    
    /**
     * Email
     */
    private String email;
    
    /**
     * Creation time
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update time
     */
    private LocalDateTime updatedAt;
    
    /**
     * Check if account is valid for transactions
     */
    public boolean isValid() {
        return accountStatus == AccountStatus.ACTIVE && !suitabilityExpired;
    }
    
    /**
     * Check if customer can buy product with given risk level
     */
    public boolean canBuyProduct(RiskLevel productRiskLevel) {
        if (!isValid()) {
            return false;
        }
        return productRiskLevel.isCompatibleWith(this.riskTolerance);
    }
}

