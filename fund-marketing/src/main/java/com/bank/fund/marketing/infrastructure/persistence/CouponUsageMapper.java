package com.bank.fund.marketing.infrastructure.persistence;

import com.bank.fund.marketing.infrastructure.persistence.po.CouponUsageRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis mapper for coupon usage
 */
@Mapper
public interface CouponUsageMapper {
    
    /**
     * Find coupon usage record by ID
     */
    CouponUsageRecordPO findById(@Param("id") String id);
    
    /**
     * Find coupon usage records by transaction serial number
     */
    List<CouponUsageRecordPO> findByTransactionSerialNumber(@Param("transactionSerialNumber") String transactionSerialNumber);
    
    /**
     * Insert coupon usage record
     */
    int insert(CouponUsageRecordPO record);
    
    /**
     * Update coupon usage record
     */
    int update(CouponUsageRecordPO record);
}

