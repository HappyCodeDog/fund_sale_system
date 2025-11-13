package com.bank.fund.trading.domain.repository;

import com.bank.fund.trading.domain.model.SubscriptionTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SubscriptionTransaction aggregate
 */
public interface SubscriptionTransactionRepository {
    
    /**
     * Find transaction by serial number
     */
    Optional<SubscriptionTransaction> findById(String transactionSerialNumber);
    
    /**
     * Save transaction
     */
    void save(SubscriptionTransaction transaction);
    
    /**
     * Update transaction
     */
    void update(SubscriptionTransaction transaction);
    
    /**
     * Check if customer has existing subscription for product
     */
    boolean hasExistingSubscription(String customerId, String productCode);
    
    /**
     * Find failed transactions that need compensation
     * Returns transactions with status FAILED and saga state indicating compensation needed
     */
    List<SubscriptionTransaction> findFailedTransactionsNeedingCompensation();
}

