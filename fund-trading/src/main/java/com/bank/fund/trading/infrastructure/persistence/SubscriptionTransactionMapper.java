package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.trading.infrastructure.persistence.po.SubscriptionTransactionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis mapper for subscription transaction
 */
@Mapper
public interface SubscriptionTransactionMapper {
    
    /**
     * Find transaction by serial number
     */
    SubscriptionTransactionPO findBySerialNumber(@Param("serialNumber") String serialNumber);
    
    /**
     * Check if customer has existing subscription for product
     */
    int countByCustomerAndProduct(@Param("customerId") String customerId, 
                                    @Param("productCode") String productCode);
    
    /**
     * Insert transaction
     */
    int insert(SubscriptionTransactionPO transaction);
    
    /**
     * Update transaction
     */
    int update(SubscriptionTransactionPO transaction);
    
    /**
     * Find failed transactions that need compensation
     * Returns transactions with status FAILED and saga state indicating compensation needed
     */
    List<SubscriptionTransactionPO> findFailedTransactionsNeedingCompensation();
    
    /**
     * Find stuck transactions that may have been interrupted during processing
     * Returns transactions in non-final state older than threshold minutes
     */
    List<SubscriptionTransactionPO> findStuckTransactionsForRecovery(@Param("minutesThreshold") int minutesThreshold);
}

