package com.bank.fund.trading.domain.service.accounting;

import com.bank.fund.common.exception.ErrorCode;
import com.bank.fund.common.exception.ExternalSystemException;
import com.bank.fund.common.integration.CoreBankingService;
import com.bank.fund.common.integration.dto.FreezeRequest;
import com.bank.fund.common.integration.dto.FreezeResponse;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Freeze strategy - used outside trading hours
 */
@Component
@Slf4j
public class FreezeStrategy implements AccountingStrategy {
    
    @Override
    public AccountingService.AccountingResult execute(SubscriptionTransaction transaction,
                                                       CoreBankingService coreBankingService) {
        log.info("Executing freeze for transaction: {}", transaction.getId());
        
        FreezeRequest request = FreezeRequest.builder()
            .customerId(transaction.getCustomerId())
            .accountNumber(transaction.getAccountNumber())
            .transactionSerialNumber(transaction.getId())
            .amount(transaction.getSubscriptionAmount().getAmount())
            .currencyCode(transaction.getSubscriptionAmount().getCurrencyCode())
            .feeAmount(transaction.getFinalFee().getAmount())
            .transactionType("FUND_SUBSCRIPTION")
            .description("Fund subscription freeze - " + transaction.getProductCode())
            .build();
        
        FreezeResponse response = coreBankingService.freeze(request);
        
        if (!response.isSuccess()) {
            log.error("Freeze operation failed for transaction {}: {}", 
                     transaction.getId(), response.getErrorMessage());
            throw new ExternalSystemException(
                ErrorCode.FREEZE_FAILED,
                "Freeze operation failed: " + response.getErrorMessage());
        }
        
        log.info("Freeze succeeded for transaction: {}, freezeId: {}", 
                 transaction.getId(), response.getFreezeId());
        
        return AccountingService.AccountingResult.builder()
            .success(true)
            .freezeId(response.getFreezeId())
            .type(AccountingService.AccountingType.FREEZE)
            .build();
    }
}

