package com.bank.fund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Fund Sale System
 */
@SpringBootApplication(scanBasePackages = "com.bank.fund")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class FundSaleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FundSaleApplication.class, args);
    }
}

