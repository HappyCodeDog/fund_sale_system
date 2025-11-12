package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.trading.infrastructure.persistence.po.SubscriptionTransactionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}

