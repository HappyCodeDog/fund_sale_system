package com.bank.fund.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Date and time utilities
 */
public final class DateTimeUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    private DateTimeUtils() {
        // Utility class
    }
    
    /**
     * Check if current time is within trading hours
     * Trading hours: 09:00:00 - 15:00:00
     */
    public static boolean isTradingTime() {
        LocalTime now = LocalTime.now();
        LocalTime tradingStart = LocalTime.of(9, 0, 0);
        LocalTime tradingEnd = LocalTime.of(15, 0, 0);
        return !now.isBefore(tradingStart) && !now.isAfter(tradingEnd);
    }
    
    /**
     * Check if current time is within trading hours (with custom hours)
     */
    public static boolean isTradingTime(LocalTime start, LocalTime end) {
        LocalTime now = LocalTime.now();
        return !now.isBefore(start) && !now.isAfter(end);
    }
    
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
}

