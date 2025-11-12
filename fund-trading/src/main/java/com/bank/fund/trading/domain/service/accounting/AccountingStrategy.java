package com.bank.fund.trading.domain.service.accounting;

import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.service.AccountingService;

/**
 * Strategy interface for different accounting scenarios
 */
public interface AccountingStrategy {
    
    /**
     * Execute accounting operation
     */
    AccountingService.AccountingResult execute(SubscriptionTransaction transaction, 
                                                CoreBankingService coreBankingService);
}

