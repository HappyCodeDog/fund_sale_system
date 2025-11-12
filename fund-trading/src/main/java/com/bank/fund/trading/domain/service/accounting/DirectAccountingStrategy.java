package com.bank.fund.trading.domain.service.accounting;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ExternalSystemException;
import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.common.integration.dto.AccountingRequest;
import com.bank.fund.common.integration.dto.AccountingResponse;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Direct accounting strategy - used during trading hours
 */
@Component
@Slf4j
public class DirectAccountingStrategy implements AccountingStrategy {
    
    @Override
    public AccountingService.AccountingResult execute(SubscriptionTransaction transaction,
                                                       CoreBankingService coreBankingService) {
        log.info("Executing direct accounting for transaction: {}", transaction.getId());
        
        AccountingRequest request = AccountingRequest.builder()
            .customerId(transaction.getCustomerId())
            .accountNumber(transaction.getAccountNumber())
            .transactionSerialNumber(transaction.getId())
            .amount(transaction.getSubscriptionAmount().getAmount())
            .currencyCode(transaction.getSubscriptionAmount().getCurrencyCode())
            .feeAmount(transaction.getFinalFee().getAmount())
            .transactionType("FUND_SUBSCRIPTION")
            .description("Fund subscription - " + transaction.getProductCode())
            .build();
        
        AccountingResponse response = coreBankingService.accounting(request);
        
        if (!response.isSuccess()) {
            log.error("Direct accounting failed for transaction {}: {}", 
                     transaction.getId(), response.getErrorMessage());
            throw new ExternalSystemException(
                ErrorCode.ACCOUNTING_FAILED,
                "Accounting operation failed: " + response.getErrorMessage());
        }
        
        log.info("Direct accounting succeeded for transaction: {}, coreTxnId: {}", 
                 transaction.getId(), response.getCoreBankingTxnId());
        
        return AccountingService.AccountingResult.builder()
            .success(true)
            .coreBankingTxnId(response.getCoreBankingTxnId())
            .type(AccountingService.AccountingType.DIRECT_ACCOUNTING)
            .build();
    }
}

