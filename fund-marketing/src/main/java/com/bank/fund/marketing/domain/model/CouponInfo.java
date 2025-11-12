package com.bank.fund.marketing.domain.model;

import com.bank.fund.common.domain.ValueObject;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Coupon information value object
 */
@Value
public class CouponInfo implements ValueObject {
    String couponId;
    String couponType;
    BigDecimal discountRate;
    BigDecimal discountAmount;
    
    /**
     * Calculate fee after applying coupon discount
     */
    public BigDecimal calculateDiscountedFee(BigDecimal originalFee) {
        if (discountRate != null) {
            // Percentage discount
            return originalFee.multiply(BigDecimal.ONE.subtract(discountRate));
        } else if (discountAmount != null) {
            // Fixed amount discount
            BigDecimal discounted = originalFee.subtract(discountAmount);
            return discounted.max(BigDecimal.ZERO); // Cannot be negative
        }
        return originalFee;
    }
}

