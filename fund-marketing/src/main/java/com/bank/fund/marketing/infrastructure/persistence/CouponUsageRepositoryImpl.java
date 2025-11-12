package com.bank.fund.marketing.infrastructure.persistence;

import com.bank.fund.marketing.domain.model.CouponUsageRecord;
import com.bank.fund.marketing.domain.model.CouponUsageStatus;
import com.bank.fund.marketing.domain.repository.CouponUsageRepository;
import com.bank.fund.marketing.infrastructure.persistence.po.CouponUsageRecordPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CouponUsageRepository using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class CouponUsageRepositoryImpl implements CouponUsageRepository {
    
    private final CouponUsageMapper couponUsageMapper;
    
    @Override
    public Optional<CouponUsageRecord> findById(String id) {
        CouponUsageRecordPO po = couponUsageMapper.findById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public List<CouponUsageRecord> findByTransactionSerialNumber(String transactionSerialNumber) {
        List<CouponUsageRecordPO> poList = couponUsageMapper.findByTransactionSerialNumber(transactionSerialNumber);
        return poList.stream().map(this::toDomain).collect(Collectors.toList());
    }
    
    @Override
    public void save(CouponUsageRecord record) {
        CouponUsageRecordPO po = toPO(record);
        couponUsageMapper.insert(po);
    }
    
    @Override
    public void update(CouponUsageRecord record) {
        CouponUsageRecordPO po = toPO(record);
        couponUsageMapper.update(po);
    }
    
    private CouponUsageRecord toDomain(CouponUsageRecordPO po) {
        return CouponUsageRecord.builder()
            .id(po.getId())
            .transactionSerialNumber(po.getTransactionSerialNumber())
            .customerId(po.getCustomerId())
            .couponId(po.getCouponId())
            .originalFee(po.getOriginalFee())
            .discountAmount(po.getDiscountAmount())
            .finalFee(po.getFinalFee())
            .status(CouponUsageStatus.valueOf(po.getStatus()))
            .usedAt(po.getUsedAt())
            .returnedAt(po.getReturnedAt())
            .createdAt(po.getCreatedAt())
            .build();
    }
    
    private CouponUsageRecordPO toPO(CouponUsageRecord record) {
        CouponUsageRecordPO po = new CouponUsageRecordPO();
        po.setId(record.getId());
        po.setTransactionSerialNumber(record.getTransactionSerialNumber());
        po.setCustomerId(record.getCustomerId());
        po.setCouponId(record.getCouponId());
        po.setOriginalFee(record.getOriginalFee());
        po.setDiscountAmount(record.getDiscountAmount());
        po.setFinalFee(record.getFinalFee());
        po.setStatus(record.getStatus().name());
        po.setUsedAt(record.getUsedAt());
        po.setReturnedAt(record.getReturnedAt());
        po.setCreatedAt(record.getCreatedAt());
        return po;
    }
}

