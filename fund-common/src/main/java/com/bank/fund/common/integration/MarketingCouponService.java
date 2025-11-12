package com.bank.fund.common.integration;

import com.bank.fund.common.integration.dto.*;

/**
 * Anti-corruption layer interface for Marketing System (Coupon Management)
 * Implementations should handle circuit breaker, timeout, and error handling
 */
public interface MarketingCouponService {
    
    /**
     * Trial calculation to determine discount amount
     * Used before actually consuming the coupon
     */
    CouponTrialResponse trialCalculate(CouponTrialRequest request);
    
    /**
     * Consume a coupon (mark as used)
     * Should be called after trial calculation succeeds
     */
    CouponUseResponse useCoupon(CouponUseRequest request);
    
    /**
     * Return/refund a coupon (compensation operation)
     * Used when transaction fails after coupon is consumed
     */
    CouponReturnResponse returnCoupon(CouponReturnRequest request);
}

