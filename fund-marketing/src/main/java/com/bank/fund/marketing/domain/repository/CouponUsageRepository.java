package com.bank.fund.marketing.domain.repository;

import com.bank.fund.marketing.domain.model.CouponUsageRecord;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for coupon usage records
 */
public interface CouponUsageRepository {
    
    /**
     * Find coupon usage record by ID
     */
    Optional<CouponUsageRecord> findById(String id);
    
    /**
     * Find coupon usage records by transaction serial number
     */
    List<CouponUsageRecord> findByTransactionSerialNumber(String transactionSerialNumber);
    
    /**
     * Save coupon usage record
     */
    void save(CouponUsageRecord record);
    
    /**
     * Update coupon usage record
     */
    void update(CouponUsageRecord record);
}

