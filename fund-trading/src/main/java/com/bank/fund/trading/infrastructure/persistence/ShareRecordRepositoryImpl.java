package com.bank.fund.trading.infrastructure.persistence;

import com.bank.fund.trading.domain.model.ShareRecord;
import com.bank.fund.trading.domain.repository.ShareRecordRepository;
import com.bank.fund.trading.infrastructure.persistence.po.ShareRecordPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of ShareRecordRepository using MyBatis
 */
@Repository
@RequiredArgsConstructor
public class ShareRecordRepositoryImpl implements ShareRecordRepository {
    
    private final ShareRecordMapper shareRecordMapper;
    
    @Override
    public Optional<ShareRecord> findByCustomerAndProduct(String customerId, String productCode) {
        ShareRecordPO po = shareRecordMapper.findByCustomerAndProduct(customerId, productCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }
    
    @Override
    public void save(ShareRecord shareRecord) {
        ShareRecordPO po = toPO(shareRecord);
        shareRecordMapper.insert(po);
    }
    
    @Override
    public void update(ShareRecord shareRecord) {
        ShareRecordPO po = toPO(shareRecord);
        shareRecordMapper.update(po);
    }
    
    private ShareRecord toDomain(ShareRecordPO po) {
        return ShareRecord.builder()
            .id(po.getId())
            .customerId(po.getCustomerId())
            .productCode(po.getProductCode())
            .shareAmount(po.getShareAmount())
            .availableAmount(po.getAvailableAmount())
            .frozenAmount(po.getFrozenAmount())
            .status(po.getStatus())
            .createdAt(po.getCreatedAt())
            .updatedAt(po.getUpdatedAt())
            .build();
    }
    
    private ShareRecordPO toPO(ShareRecord shareRecord) {
        ShareRecordPO po = new ShareRecordPO();
        po.setId(shareRecord.getId());
        po.setCustomerId(shareRecord.getCustomerId());
        po.setProductCode(shareRecord.getProductCode());
        po.setShareAmount(shareRecord.getShareAmount());
        po.setAvailableAmount(shareRecord.getAvailableAmount());
        po.setFrozenAmount(shareRecord.getFrozenAmount());
        po.setStatus(shareRecord.getStatus());
        po.setCreatedAt(shareRecord.getCreatedAt());
        po.setUpdatedAt(shareRecord.getUpdatedAt());
        return po;
    }
}

