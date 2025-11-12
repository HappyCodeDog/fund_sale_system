package com.bank.fund.customer.domain.repository;

import com.bank.fund.customer.domain.model.CustomerAccount;

import java.util.Optional;

/**
 * Repository interface for CustomerAccount aggregate
 */
public interface CustomerAccountRepository {
    
    /**
     * Find customer account by customer ID
     */
    Optional<CustomerAccount> findById(String customerId);
    
    /**
     * Find customer account by account number
     */
    Optional<CustomerAccount> findByAccountNumber(String accountNumber);
    
    /**
     * Save customer account
     */
    void save(CustomerAccount account);
    
    /**
     * Check if customer exists
     */
    boolean exists(String customerId);
}

