package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.trading.infrastructure.persistence.po.ShareRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis mapper for share record
 */
@Mapper
public interface ShareRecordMapper {
    
    /**
     * Find share record by customer and product
     */
    ShareRecordPO findByCustomerAndProduct(@Param("customerId") String customerId,
                                            @Param("productCode") String productCode);
    
    /**
     * Insert share record
     */
    int insert(ShareRecordPO shareRecord);
    
    /**
     * Update share record
     */
    int update(ShareRecordPO shareRecord);
}

