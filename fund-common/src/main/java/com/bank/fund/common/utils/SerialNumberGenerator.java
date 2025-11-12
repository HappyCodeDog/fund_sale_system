package com.bank.fund.common.utils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Serial number generator for transaction IDs
 * Format: YYYYMMDDHHMMSS + 6-digit sequence
 */
public class SerialNumberGenerator {
    
    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static final int MAX_SEQUENCE = 999999;
    
    /**
     * Generate unique serial number
     * Format: YYYYMMDDHHMMSS + 6-digit sequence number
     */
    public static String generate() {
        String timestamp = DateTimeUtils.formatDateTime(LocalDateTime.now());
        int seq = sequence.incrementAndGet();
        if (seq > MAX_SEQUENCE) {
            sequence.set(1);
            seq = 1;
        }
        return timestamp + String.format("%06d", seq);
    }
    
    /**
     * Generate serial number with prefix
     */
    public static String generate(String prefix) {
        return prefix + generate();
    }
}

