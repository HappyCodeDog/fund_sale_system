package com.bank.fund.trading.domain.service.accounting;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ExternalSystemException;
import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.common.integration.dto.AccountingResponse;
import com.bank.fund.common.integration.dto.ExchangeAndAccountingRequest;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Exchange and accounting strategy - used when currency conversion is required
 * Note: Cannot be reversed due to exchange rate volatility (risk disclosed to customer)
 */
@Component
@Slf4j
public class ExchangeAndAccountingStrategy implements AccountingStrategy {
    
    @Override
    public AccountingService.AccountingResult execute(SubscriptionTransaction transaction,
                                                       CoreBankingService coreBankingService) {
        log.info("Executing exchange and accounting for transaction: {}", transaction.getId());
        log.warn("Exchange operation cannot be reversed - exchange rate risk applies");
        
        // Assuming account currency is different from product currency
        // In real implementation, this should be passed as parameter
        String accountCurrency = transaction.getSubscriptionAmount().getCurrencyCode();
        // Target currency should come from product
        
        ExchangeAndAccountingRequest request = ExchangeAndAccountingRequest.builder()
            .customerId(transaction.getCustomerId())
            .accountNumber(transaction.getAccountNumber())
            .transactionSerialNumber(transaction.getId())
            .sourceCurrency(accountCurrency)
            .targetCurrency(transaction.getSubscriptionAmount().getCurrencyCode())
            .sourceAmount(transaction.getSubscriptionAmount().getAmount())
            .feeAmount(transaction.getFinalFee().getAmount())
            .transactionType("FUND_SUBSCRIPTION_FX")
            .description("Fund subscription with currency exchange - " + transaction.getProductCode())
            .build();
        
        AccountingResponse response = coreBankingService.exchangeAndAccounting(request);
        
        if (!response.isSuccess()) {
            log.error("Exchange and accounting failed for transaction {}: {}", 
                     transaction.getId(), response.getErrorMessage());
            throw new ExternalSystemException(
                ErrorCode.EXCHANGE_FAILED,
                "Exchange and accounting operation failed: " + response.getErrorMessage());
        }
        
        log.info("Exchange and accounting succeeded for transaction: {}, coreTxnId: {}", 
                 transaction.getId(), response.getCoreBankingTxnId());
        
        return AccountingService.AccountingResult.builder()
            .success(true)
            .coreBankingTxnId(response.getCoreBankingTxnId())
            .type(AccountingService.AccountingType.EXCHANGE_AND_ACCOUNTING)
            .build();
    }
}

