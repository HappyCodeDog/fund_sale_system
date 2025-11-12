package com.bank.fund.product.infrastructure.persistence;

import com.bank.fund.common.money.Money;
import com.bank.fund.product.domain.model.FundProduct;
import com.bank.fund.product.domain.model.ProductStatus;
import com.bank.fund.product.domain.model.RiskLevel;
import com.bank.fund.product.domain.model.TransactionStatus;
import com.bank.fund.product.domain.repository.FundProductRepository;
import com.bank.fund.product.infrastructure.persistence.po.FundProductPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.Optional;

/**
 * Implementation of FundProductRepository using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class FundProductRepositoryImpl implements FundProductRepository {
    
    private final ProductMapper productMapper;
    
    @Override
    public Optional<FundProduct> findById(String productCode) {
        FundProductPO po = productMapper.findByProductCode(productCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public void save(FundProduct product) {
        FundProductPO po = toPO(product);
        if (productMapper.findByProductCode(product.getId()) == null) {
            productMapper.insert(po);
        } else {
            productMapper.update(po);
        }
    }
    
    @Override
    public boolean exists(String productCode) {
        return productMapper.findByProductCode(productCode) != null;
    }
    
    private FundProduct toDomain(FundProductPO po) {
        Currency currency = Currency.getInstance(po.getCurrencyCode());
        
        return FundProduct.builder()
            .id(po.getProductCode())
            .productName(po.getProductName())
            .productStatus(ProductStatus.valueOf(po.getProductStatus()))
            .transactionStatus(TransactionStatus.valueOf(po.getTransactionStatus()))
            .riskLevel(new RiskLevel(po.getRiskLevel()))
            .minInitialAmount(new Money(po.getMinInitialAmount(), currency))
            .minAdditionalAmount(new Money(po.getMinAdditionalAmount(), currency))
            .maxSubscriptionAmount(po.getMaxSubscriptionAmount() != null ? 
                new Money(po.getMaxSubscriptionAmount(), currency) : null)
            .amountUnit(po.getAmountUnit())
            .dailyTaQuota(po.getDailyTaQuota() != null ? 
                new Money(po.getDailyTaQuota(), currency) : null)
            .allowedChannels(po.getAllowedChannels())
            .currencyCode(po.getCurrencyCode())
            .subscriptionFeeRate(po.getSubscriptionFeeRate())
            .createdAt(po.getCreatedAt())
            .updatedAt(po.getUpdatedAt())
            .build();
    }
    
    private FundProductPO toPO(FundProduct product) {
        FundProductPO po = new FundProductPO();
        po.setProductCode(product.getId());
        po.setProductName(product.getProductName());
        po.setProductStatus(product.getProductStatus().name());
        po.setTransactionStatus(product.getTransactionStatus().name());
        po.setRiskLevel(product.getRiskLevel().getLevel());
        po.setMinInitialAmount(product.getMinInitialAmount().getAmount());
        po.setMinAdditionalAmount(product.getMinAdditionalAmount().getAmount());
        po.setMaxSubscriptionAmount(product.getMaxSubscriptionAmount() != null ? 
            product.getMaxSubscriptionAmount().getAmount() : null);
        po.setAmountUnit(product.getAmountUnit());
        po.setDailyTaQuota(product.getDailyTaQuota() != null ? 
            product.getDailyTaQuota().getAmount() : null);
        po.setAllowedChannels(product.getAllowedChannels());
        po.setCurrencyCode(product.getCurrencyCode());
        po.setSubscriptionFeeRate(product.getSubscriptionFeeRate());
        po.setCreatedAt(product.getCreatedAt());
        po.setUpdatedAt(product.getUpdatedAt());
        return po;
    }
}

