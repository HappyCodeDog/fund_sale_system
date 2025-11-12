package com.bank.fund.marketing.domain.service;

import com.bank.fund.common.money.Money;
import com.bank.fund.marketing.domain.model.CouponInfo;
import com.bank.fund.marketing.domain.model.FeeCalculation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Domain service for fee calculation
 */
@Service
public class FeeCalculationService {
    
    /**
     * Calculate subscription fee without coupon
     */
    public FeeCalculation calculateFee(Money subscriptionAmount, BigDecimal feeRate) {
        return FeeCalculation.calculateWithoutCoupon(subscriptionAmount, feeRate);
    }
    
    /**
     * Calculate subscription fee with coupon
     */
    public FeeCalculation calculateFeeWithCoupon(Money subscriptionAmount, 
                                                   BigDecimal feeRate, 
                                                   CouponInfo coupon) {
        return FeeCalculation.calculateWithCoupon(subscriptionAmount, feeRate, coupon);
    }
}

