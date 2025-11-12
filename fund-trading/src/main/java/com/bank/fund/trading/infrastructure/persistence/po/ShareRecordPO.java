package com.bank.fund.trading.infrastructure.persistence.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistent object for share record table
 */
@Data
public class ShareRecordPO {
    private String id;
    private String customerId;
    private String productCode;
    private BigDecimal shareAmount;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

