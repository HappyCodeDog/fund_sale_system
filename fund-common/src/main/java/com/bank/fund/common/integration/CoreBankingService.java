package com.bank.fund.common.integration;

import com.bank.fund.common.integration.dto.*;

/**
 * Anti-corruption layer interface for Core Banking System
 * Implementations should handle circuit breaker, timeout, and error handling
 */
public interface CoreBankingService {
    
    /**
     * Perform direct accounting (debit customer account)
     * Used during trading hours
     */
    AccountingResponse accounting(AccountingRequest request);
    
    /**
     * Freeze funds for later processing
     * Used outside trading hours
     */
    FreezeResponse freeze(FreezeRequest request);
    
    /**
     * Unfreeze funds (compensation operation)
     */
    AccountingResponse unfreeze(String freezeId, String transactionSerialNumber);
    
    /**
     * Exchange currency and perform accounting in one operation
     * Used when customer's account currency differs from product currency
     */
    AccountingResponse exchangeAndAccounting(ExchangeAndAccountingRequest request);
    
    /**
     * Reverse an accounting transaction (compensation operation)
     * Used when transaction fails after accounting succeeds
     */
    AccountingResponse reversal(String coreBankingTxnId, String transactionSerialNumber);
}

