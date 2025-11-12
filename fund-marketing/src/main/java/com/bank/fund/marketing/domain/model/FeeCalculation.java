package com.bank.fund.marketing.domain.model;

import com.bank.fund.common.domain.ValueObject;
import com.bank.fund.common.money.Money;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Fee calculation result value object
 */
@Value
public class FeeCalculation implements ValueObject {
    Money subscriptionAmount;
    BigDecimal feeRate;
    Money originalFee;
    Money discountAmount;
    Money finalFee;
    
    /**
     * Calculate fee without coupon
     */
    public static FeeCalculation calculateWithoutCoupon(Money amount, BigDecimal feeRate) {
        Money fee = amount.multiply(feeRate);
        return new FeeCalculation(
            amount,
            feeRate,
            fee,
            new Money(BigDecimal.ZERO, amount.getCurrency()),
            fee
        );
    }
    
    /**
     * Calculate fee with coupon discount
     */
    public static FeeCalculation calculateWithCoupon(Money amount, BigDecimal feeRate, CouponInfo coupon) {
        Money originalFee = amount.multiply(feeRate);
        BigDecimal discountedFeeAmount = coupon.calculateDiscountedFee(originalFee.getAmount());
        Money finalFee = new Money(discountedFeeAmount, amount.getCurrency());
        Money discount = originalFee.subtract(finalFee);
        
        return new FeeCalculation(
            amount,
            feeRate,
            originalFee,
            discount,
            finalFee
        );
    }
    
    /**
     * Get effective fee rate after discount
     */
    public BigDecimal getEffectiveFeeRate() {
        if (subscriptionAmount.isZero()) {
            return BigDecimal.ZERO;
        }
        return finalFee.getAmount()
            .divide(subscriptionAmount.getAmount(), 6, RoundingMode.HALF_UP);
    }
}

