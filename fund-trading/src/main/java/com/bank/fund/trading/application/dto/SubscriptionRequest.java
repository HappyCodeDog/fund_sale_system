package com.bank.fund.trading.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Request DTO for fund subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Product code is required")
    private String productCode;
    
    @NotNull(message = "Subscription amount is required")
    @Positive(message = "Subscription amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency code is required")
    private String currencyCode;
    
    /**
     * Optional coupon ID
     */
    private String couponId;
    
    @NotBlank(message = "Channel is required")
    private String channel;
}

