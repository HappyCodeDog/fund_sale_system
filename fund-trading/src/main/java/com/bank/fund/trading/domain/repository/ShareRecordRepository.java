package com.bank.fund.trading.domain.repository;

import com.bank.fund.trading.domain.model.ShareRecord;

import java.util.Optional;

/**
 * Repository interface for ShareRecord
 */
public interface ShareRecordRepository {
    
    /**
     * Find share record by customer and product
     */
    Optional<ShareRecord> findByCustomerAndProduct(String customerId, String productCode);
    
    /**
     * Save share record
     */
    void save(ShareRecord shareRecord);
    
    /**
     * Update share record
     */
    void update(ShareRecord shareRecord);
}

