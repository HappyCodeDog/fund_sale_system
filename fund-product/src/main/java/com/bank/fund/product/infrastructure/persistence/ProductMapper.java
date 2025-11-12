package com.bank.fund.product.infrastructure.persistence;

import com.bank.fund.product.infrastructure.persistence.po.FundProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis mapper for fund product
 */
@Mapper
public interface ProductMapper {
    
    /**
     * Find product by product code
     */
    FundProductPO findByProductCode(@Param("productCode") String productCode);
    
    /**
     * Insert product
     */
    int insert(FundProductPO product);
    
    /**
     * Update product
     */
    int update(FundProductPO product);
}

