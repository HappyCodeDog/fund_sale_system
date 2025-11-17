package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.common.money.Money;
import com.bank.fund.trading.domain.model.SagaState;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.model.TransactionStatus;
import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import com.bank.fund.trading.infrastructure.persistence.po.SubscriptionTransactionPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SubscriptionTransactionRepository using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class SubscriptionTransactionRepositoryImpl implements SubscriptionTransactionRepository {
    
    private final SubscriptionTransactionMapper subscriptionTransactionMapper;
    
    @Override
    public Optional<SubscriptionTransaction> findById(String transactionSerialNumber) {
        SubscriptionTransactionPO po = subscriptionTransactionMapper.findBySerialNumber(transactionSerialNumber);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public void save(SubscriptionTransaction transaction) {
        SubscriptionTransactionPO po = toPO(transaction);
        subscriptionTransactionMapper.insert(po);
    }
    
    @Override
    public void update(SubscriptionTransaction transaction) {
        SubscriptionTransactionPO po = toPO(transaction);
        subscriptionTransactionMapper.update(po);
    }
    
    @Override
    public boolean hasExistingSubscription(String customerId, String productCode) {
        int count = subscriptionTransactionMapper.countByCustomerAndProduct(customerId, productCode);
        return count > 0;
    }
    
    @Override
    public List<SubscriptionTransaction> findFailedTransactionsNeedingCompensation() {
        List<SubscriptionTransactionPO> pos = subscriptionTransactionMapper.findFailedTransactionsNeedingCompensation();
        return pos.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SubscriptionTransaction> findStuckTransactionsForRecovery(int minutesThreshold) {
        List<SubscriptionTransactionPO> pos = subscriptionTransactionMapper.findStuckTransactionsForRecovery(minutesThreshold);
        return pos.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    private SubscriptionTransaction toDomain(SubscriptionTransactionPO po) {
        Currency currency = Currency.getInstance(po.getCurrencyCode());
        
        return SubscriptionTransaction.builder()
            .id(po.getSerialNumber())
            .customerId(po.getCustomerId())
            .accountNumber(po.getAccountNumber())
            .productCode(po.getProductCode())
            .subscriptionAmount(new Money(po.getSubscriptionAmount(), currency))
            .feeRate(po.getFeeRate())
            .originalFee(new Money(po.getOriginalFee(), currency))
            .discountAmount(new Money(po.getDiscountAmount(), currency))
            .finalFee(new Money(po.getFinalFee(), currency))
            .couponId(po.getCouponId())
            .marketingUsageId(po.getMarketingUsageId())
            .channel(po.getChannel())
            .status(TransactionStatus.valueOf(po.getStatus()))
            .coreBankingTxnId(po.getCoreBankingTxnId())
            .freezeId(po.getFreezeId())
            .sagaState(SagaState.valueOf(po.getSagaState()))
            .errorCode(po.getErrorCode())
            .errorMessage(po.getErrorMessage())
            .firstTimeSubscription("Y".equals(po.getFirstTimeSubscription()))
            .requestTime(po.getRequestTime())
            .completionTime(po.getCompletionTime())
            .createdAt(po.getCreatedAt())
            .updatedAt(po.getUpdatedAt())
            .build();
    }
    
    private SubscriptionTransactionPO toPO(SubscriptionTransaction transaction) {
        SubscriptionTransactionPO po = new SubscriptionTransactionPO();
        po.setSerialNumber(transaction.getId());
        po.setCustomerId(transaction.getCustomerId());
        po.setAccountNumber(transaction.getAccountNumber());
        po.setProductCode(transaction.getProductCode());
        po.setSubscriptionAmount(transaction.getSubscriptionAmount().getAmount());
        po.setCurrencyCode(transaction.getSubscriptionAmount().getCurrencyCode());
        po.setFeeRate(transaction.getFeeRate());
        po.setOriginalFee(transaction.getOriginalFee().getAmount());
        po.setDiscountAmount(transaction.getDiscountAmount().getAmount());
        po.setFinalFee(transaction.getFinalFee().getAmount());
        po.setCouponId(transaction.getCouponId());
        po.setMarketingUsageId(transaction.getMarketingUsageId());
        po.setChannel(transaction.getChannel());
        po.setStatus(transaction.getStatus().name());
        po.setCoreBankingTxnId(transaction.getCoreBankingTxnId());
        po.setFreezeId(transaction.getFreezeId());
        po.setSagaState(transaction.getSagaState().name());
        po.setErrorCode(transaction.getErrorCode());
        po.setErrorMessage(transaction.getErrorMessage());
        po.setFirstTimeSubscription(transaction.isFirstTimeSubscription() ? "Y" : "N");
        po.setRequestTime(transaction.getRequestTime());
        po.setCompletionTime(transaction.getCompletionTime());
        po.setCreatedAt(transaction.getCreatedAt());
        po.setUpdatedAt(transaction.getUpdatedAt());
        return po;
    }
}

