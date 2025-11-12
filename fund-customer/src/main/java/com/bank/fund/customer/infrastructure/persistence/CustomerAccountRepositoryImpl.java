package com.bank.fund.customer.infrastructure.persistence;

import com.bank.fund.customer.domain.model.AccountStatus;
import com.bank.fund.customer.domain.model.CustomerAccount;
import com.bank.fund.customer.domain.model.CustomerType;
import com.bank.fund.customer.domain.repository.CustomerAccountRepository;
import com.bank.fund.customer.infrastructure.persistence.po.CustomerAccountPO;
import com.bank.fund.product.domain.model.RiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of CustomerAccountRepository using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class CustomerAccountRepositoryImpl implements CustomerAccountRepository {
    
    private final CustomerAccountMapper customerAccountMapper;
    
    @Override
    public Optional<CustomerAccount> findById(String customerId) {
        CustomerAccountPO po = customerAccountMapper.findByCustomerId(customerId);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public Optional<CustomerAccount> findByAccountNumber(String accountNumber) {
        CustomerAccountPO po = customerAccountMapper.findByAccountNumber(accountNumber);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public void save(CustomerAccount account) {
        CustomerAccountPO po = toPO(account);
        if (customerAccountMapper.findByCustomerId(account.getId()) == null) {
            customerAccountMapper.insert(po);
        } else {
            customerAccountMapper.update(po);
        }
    }
    
    @Override
    public boolean exists(String customerId) {
        return customerAccountMapper.findByCustomerId(customerId) != null;
    }
    
    private CustomerAccount toDomain(CustomerAccountPO po) {
        return CustomerAccount.builder()
            .id(po.getCustomerId())
            .customerName(po.getCustomerName())
            .customerType(CustomerType.valueOf(po.getCustomerType()))
            .accountNumber(po.getAccountNumber())
            .accountStatus(AccountStatus.valueOf(po.getAccountStatus()))
            .riskTolerance(new RiskLevel(po.getRiskTolerance()))
            .suitabilityAssessmentDate(po.getSuitabilityAssessmentDate())
            .suitabilityExpired("Y".equals(po.getSuitabilityExpired()))
            .idType(po.getIdType())
            .idNumber(po.getIdNumber())
            .phoneNumber(po.getPhoneNumber())
            .email(po.getEmail())
            .createdAt(po.getCreatedAt())
            .updatedAt(po.getUpdatedAt())
            .build();
    }
    
    private CustomerAccountPO toPO(CustomerAccount account) {
        CustomerAccountPO po = new CustomerAccountPO();
        po.setCustomerId(account.getId());
        po.setCustomerName(account.getCustomerName());
        po.setCustomerType(account.getCustomerType().name());
        po.setAccountNumber(account.getAccountNumber());
        po.setAccountStatus(account.getAccountStatus().name());
        po.setRiskTolerance(account.getRiskTolerance().getLevel());
        po.setSuitabilityAssessmentDate(account.getSuitabilityAssessmentDate());
        po.setSuitabilityExpired(account.isSuitabilityExpired() ? "Y" : "N");
        po.setIdType(account.getIdType());
        po.setIdNumber(account.getIdNumber());
        po.setPhoneNumber(account.getPhoneNumber());
        po.setEmail(account.getEmail());
        po.setCreatedAt(account.getCreatedAt());
        po.setUpdatedAt(account.getUpdatedAt());
        return po;
    }
}

