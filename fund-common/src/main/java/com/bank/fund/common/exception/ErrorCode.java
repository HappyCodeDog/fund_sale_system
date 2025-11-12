package com.bank.fund.common.exception;

/**
 * Centralized error codes
 */
public final class ErrorCode {
    
    // System errors (0xxx)
    public static final String SYSTEM_ERROR = "0001";
    public static final String EXTERNAL_SYSTEM_TIMEOUT = "0002";
    public static final String EXTERNAL_SYSTEM_ERROR = "0003";
    
    // Validation errors (1xxx)
    public static final String INVALID_PARAMETER = "1001";
    public static final String PRODUCT_NOT_FOUND = "1101";
    public static final String PRODUCT_STATUS_INVALID = "1102";
    public static final String CHANNEL_NOT_ALLOWED = "1103";
    
    public static final String CUSTOMER_NOT_FOUND = "1201";
    public static final String ACCOUNT_INVALID = "1202";
    public static final String RISK_LEVEL_MISMATCH = "1203";
    
    public static final String AMOUNT_TOO_LOW = "1301";
    public static final String AMOUNT_TOO_HIGH = "1302";
    public static final String AMOUNT_INVALID_UNIT = "1303";
    public static final String QUOTA_EXCEEDED = "1304";
    
    // Business errors (2xxx)
    public static final String COUPON_TRIAL_FAILED = "2001";
    public static final String COUPON_USE_FAILED = "2002";
    public static final String COUPON_RETURN_FAILED = "2003";
    
    public static final String ACCOUNTING_FAILED = "2101";
    public static final String FREEZE_FAILED = "2102";
    public static final String EXCHANGE_FAILED = "2103";
    public static final String REVERSAL_FAILED = "2104";
    public static final String UNFREEZE_FAILED = "2105";
    
    public static final String SERIAL_NUMBER_GENERATION_FAILED = "2201";
    public static final String TRANSACTION_SAVE_FAILED = "2202";
    
    private ErrorCode() {
        // Utility class
    }
}

