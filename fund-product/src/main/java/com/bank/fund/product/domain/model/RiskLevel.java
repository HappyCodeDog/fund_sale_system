package com.bank.fund.product.domain.model;

import com.bank.fund.common.domain.ValueObject;
import lombok.Value;

/**
 * Risk level value object (1-5)
 */
@Value
public class RiskLevel implements ValueObject {
    int level;
    
    public RiskLevel(int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Risk level must be between 1 and 5");
        }
        this.level = level;
    }
    
    /**
     * Check if this risk level is compatible with customer's risk tolerance
     * Customer can only buy products with risk level <= their tolerance
     */
    public boolean isCompatibleWith(RiskLevel customerTolerance) {
        return this.level <= customerTolerance.level;
    }
    
    public boolean isHigherThan(RiskLevel other) {
        return this.level > other.level;
    }
    
    public boolean isLowerThan(RiskLevel other) {
        return this.level < other.level;
    }
}

