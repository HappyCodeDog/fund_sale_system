package com.bank.fund.trading.domain.service;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ExternalSystemException;
import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.common.integration.dto.*;
import com.bank.fund.common.money.Money;
import com.bank.fund.common.utils.DateTimeUtils;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.service.accounting.AccountingStrategy;
import com.bank.fund.trading.domain.service.accounting.DirectAccountingStrategy;
import com.bank.fund.trading.domain.service.accounting.ExchangeAndAccountingStrategy;
import com.bank.fund.trading.domain.service.accounting.FreezeStrategy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Domain service for orchestrating accounting operations
 * Uses Strategy pattern to handle different accounting scenarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingService {
    
    private final CoreBankingService coreBankingService;
    private final DirectAccountingStrategy directAccountingStrategy;
    private final FreezeStrategy freezeStrategy;
    private final ExchangeAndAccountingStrategy exchangeAndAccountingStrategy;
    
    /**
     * Execute accounting operation based on conditions
     */
    @CircuitBreaker(name = "coreBank", fallbackMethod = "accountingFallback")
    public AccountingResult executeAccounting(SubscriptionTransaction transaction,
                                               String productCurrency,
                                               String accountCurrency) {
        log.info("Executing accounting for transaction: {}", transaction.getId());
        
        AccountingStrategy strategy = selectStrategy(productCurrency, accountCurrency);
        log.info("Selected strategy: {}", strategy.getClass().getSimpleName());
        
        return strategy.execute(transaction, coreBankingService);
    }
    
    /**
     * Select appropriate accounting strategy
     */
    private AccountingStrategy selectStrategy(String productCurrency, String accountCurrency) {
        // Strategy 1: Currency exchange required
        if (!productCurrency.equals(accountCurrency)) {
            return exchangeAndAccountingStrategy;
        }
        
        // Strategy 2: Direct accounting during trading hours
        if (DateTimeUtils.isTradingTime()) {
            return directAccountingStrategy;
        }
        
        // Strategy 3: Freeze outside trading hours
        return freezeStrategy;
    }
    
    /**
     * Fallback method for circuit breaker
     */
    private AccountingResult accountingFallback(SubscriptionTransaction transaction,
                                                  String productCurrency,
                                                  String accountCurrency,
                                                  Exception e) {
        log.error("Accounting operation failed with circuit breaker open for transaction: {}", 
                  transaction.getId(), e);
        throw new ExternalSystemException(
            ErrorCode.EXTERNAL_SYSTEM_TIMEOUT,
            "Core banking system is temporarily unavailable",
            e);
    }
    
    /**
     * Result of accounting operation
     */
    @lombok.Value
    @lombok.Builder
    public static class AccountingResult {
        boolean success;
        String coreBankingTxnId;
        String freezeId;
        AccountingType type;
        String errorCode;
        String errorMessage;
    }
    
    /**
     * Type of accounting operation performed
     */
    public enum AccountingType {
        DIRECT_ACCOUNTING,
        FREEZE,
        EXCHANGE_AND_ACCOUNTING
    }
}

