package com.bank.fund.trading.domain.model;

import com.bank.fund.common.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Share record entity
 * Tracks customer's fund shares
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareRecord implements Entity<String> {
    
    /**
     * Share record ID
     */
    private String id;
    
    /**
     * Customer ID
     */
    private String customerId;
    
    /**
     * Product code
     */
    private String productCode;
    
    /**
     * Share amount (initially 0, updated after TA confirmation)
     */
    private BigDecimal shareAmount;
    
    /**
     * Available share amount
     */
    private BigDecimal availableAmount;
    
    /**
     * Frozen share amount
     */
    private BigDecimal frozenAmount;
    
    /**
     * Status
     */
    private String status;
    
    /**
     * Creation time
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update time
     */
    private LocalDateTime updatedAt;
    
    /**
     * Initialize with zero shares
     */
    public static ShareRecord createNew(String customerId, String productCode) {
        return ShareRecord.builder()
            .customerId(customerId)
            .productCode(productCode)
            .shareAmount(BigDecimal.ZERO)
            .availableAmount(BigDecimal.ZERO)
            .frozenAmount(BigDecimal.ZERO)
            .status("ACTIVE")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}

