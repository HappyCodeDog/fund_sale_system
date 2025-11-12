package com.bank.fund.product.domain.repository;

import com.bank.fund.product.domain.model.FundProduct;

import java.util.Optional;

/**
 * Repository interface for FundProduct aggregate
 */
public interface FundProductRepository {
    
    /**
     * Find product by product code
     */
    Optional<FundProduct> findById(String productCode);
    
    /**
     * Save product
     */
    void save(FundProduct product);
    
    /**
     * Check if product exists
     */
    boolean exists(String productCode);
}

