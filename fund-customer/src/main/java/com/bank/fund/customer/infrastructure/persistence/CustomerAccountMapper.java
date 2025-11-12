package com.bank.fund.customer.infrastructure.persistence;

import com.bank.fund.customer.infrastructure.persistence.po.CustomerAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis mapper for customer account
 */
@Mapper
public interface CustomerAccountMapper {
    
    /**
     * Find customer account by customer ID
     */
    CustomerAccountPO findByCustomerId(@Param("customerId") String customerId);
    
    /**
     * Find customer account by account number
     */
    CustomerAccountPO findByAccountNumber(@Param("accountNumber") String accountNumber);
    
    /**
     * Insert customer account
     */
    int insert(CustomerAccountPO account);
    
    /**
     * Update customer account
     */
    int update(CustomerAccountPO account);
}

