package com.bank.fund.common.domain;

/**
 * Marker interface for Domain Entities
 * Entities have identity and lifecycle
 */
public interface Entity<ID> {
    ID getId();
}

