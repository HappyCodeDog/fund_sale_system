package com.bank.fund.trading.domain.model;

import com.bank.fund.common.domain.ValueObject;
import com.bank.fund.common.money.Money;
import lombok.Value;

/**
 * Transaction amount value object
 * Encapsulates subscription amount with validation
 */
@Value
public class TransactionAmount implements ValueObject {
    Money amount;
    
    public TransactionAmount(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
    }
    
    public boolean isGreaterThan(Money other) {
        return amount.isGreaterThan(other);
    }
    
    public boolean isLessThan(Money other) {
        return amount.isLessThan(other);
    }
}

