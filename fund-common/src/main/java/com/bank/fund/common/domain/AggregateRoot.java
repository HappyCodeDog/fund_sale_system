package com.bank.fund.common.domain;

/**
 * Marker interface for Aggregate Roots
 * Aggregate roots are the entry point for operations on the aggregate
 */
public interface AggregateRoot<ID> extends Entity<ID> {
}

