package com.bank.fund.customer.infrastructure.persistence.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Persistent object for customer account table
 */
@Data
public class CustomerAccountPO {
    private String customerId;
    private String customerName;
    private String customerType;
    private String accountNumber;
    private String accountStatus;
    private Integer riskTolerance;
    private LocalDateTime suitabilityAssessmentDate;
    private String suitabilityExpired;
    private String idType;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

