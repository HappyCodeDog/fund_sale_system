package com.bank.fund.trading.application;

import com.bank.fund.trading.domain.model.SagaState;
import com.bank.fund.trading.domain.model.SubscriptionTransaction;
import com.bank.fund.trading.domain.model.TransactionStatus;
import com.bank.fund.trading.domain.repository.SubscriptionTransactionRepository;
import com.bank.fund.trading.domain.service.TransactionRollbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task for compensating failed transactions
 * Scans for failed transactions that need compensation and triggers async compensation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompensationScheduler {
    
    private final SubscriptionTransactionRepository transactionRepository;
    private final TransactionRollbackService rollbackService;
    
    /**
     * Scan and compensate failed transactions
     * Runs every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes = 300000 milliseconds
    public void compensateFailedTransactions() {
        log.info("Starting scheduled compensation scan for failed transactions");
        
        try {
            // First, check for stuck transactions that may need recovery
            checkStuckTransactions();
        } catch (Exception e) {
            log.error("Error during stuck transaction check", e);
        }
        
        try {
            List<SubscriptionTransaction> failedTransactions = 
                transactionRepository.findFailedTransactionsNeedingCompensation();
            
            if (failedTransactions.isEmpty()) {
                log.debug("No failed transactions found that need compensation");
                return;
            }
            
            log.info("Found {} failed transactions that need compensation", failedTransactions.size());
            
            for (SubscriptionTransaction transaction : failedTransactions) {
                try {
                    log.info("Triggering compensation for transaction: {}, sagaState: {}", 
                             transaction.getId(), transaction.getSagaState());
                    
                    // Save original saga state for recovery if compensation fails
                    SagaState originalSagaState = transaction.getSagaState();
                    
                    // Mark as compensating to avoid duplicate processing
                    transaction.setSagaState(SagaState.COMPENSATING);
                    transaction.setStatus(TransactionStatus.COMPENSATING);
                    transaction.setUpdatedAt(LocalDateTime.now());
                    transactionRepository.update(transaction);
                    
                    // Trigger async compensation
                    rollbackService.compensate(transaction)
                        .thenAccept(result -> {
                            if (result.isSuccess()) {
                                log.info("Scheduled compensation completed successfully for transaction: {}", 
                                        transaction.getId());
                                // Update saga state to COMPENSATION_COMPLETED
                                transaction.setSagaState(SagaState.COMPENSATION_COMPLETED);
                                transaction.setUpdatedAt(LocalDateTime.now());
                                transactionRepository.update(transaction);
                            } else {
                                log.error("Scheduled compensation failed for transaction: {}, error: {}", 
                                         transaction.getId(), result.getErrorMessage());
                                // Reset to original saga state so it can be retried
                                transaction.setSagaState(originalSagaState);
                                transaction.setStatus(TransactionStatus.FAILED);
                                transaction.setUpdatedAt(LocalDateTime.now());
                                transactionRepository.update(transaction);
                            }
                        })
                        .exceptionally(ex -> {
                            log.error("Exception during scheduled compensation for transaction: {}", 
                                     transaction.getId(), ex);
                            // Reset to original saga state so it can be retried
                            transaction.setSagaState(originalSagaState);
                            transaction.setStatus(TransactionStatus.FAILED);
                            transaction.setUpdatedAt(LocalDateTime.now());
                            transactionRepository.update(transaction);
                            return null;
                        });
                    
                } catch (Exception e) {
                    log.error("Failed to trigger compensation for transaction: {}", 
                             transaction.getId(), e);
                }
            }
            
            log.info("Completed scheduled compensation scan, processed {} transactions", 
                     failedTransactions.size());
            
        } catch (Exception e) {
            log.error("Error during scheduled compensation scan", e);
        }
    }
    
    /**
     * Check for stuck transactions that may have been interrupted during processing
     * These could be transactions where accounting succeeded but process crashed before update
     */
    private void checkStuckTransactions() {
        log.debug("Checking for stuck transactions");
        
        // Find transactions stuck for more than 10 minutes
        List<SubscriptionTransaction> stuckTransactions = 
            transactionRepository.findStuckTransactionsForRecovery(10);
        
        if (stuckTransactions.isEmpty()) {
            log.debug("No stuck transactions found");
            return;
        }
        
        log.warn("Found {} stuck transactions that may need manual review or recovery", 
                 stuckTransactions.size());
        
        for (SubscriptionTransaction transaction : stuckTransactions) {
            log.warn("Stuck transaction detected - ID: {}, Status: {}, SagaState: {}, " +
                     "CoreBankingTxnId: {}, FreezeId: {}, UpdatedAt: {}. " +
                     "This may indicate a process crash after accounting. Manual review recommended.", 
                     transaction.getId(), transaction.getStatus(), transaction.getSagaState(),
                     transaction.getCoreBankingTxnId(), transaction.getFreezeId(), 
                     transaction.getUpdatedAt());
            
            // Mark as failed to trigger compensation on next scan
            // This handles the case where accounting may have succeeded but wasn't recorded
            try {
                transaction.markFailed("STUCK_TRANSACTION", 
                    "Transaction stuck in non-final state, marked for compensation");
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.update(transaction);
                log.info("Marked stuck transaction {} as FAILED for compensation", transaction.getId());
            } catch (Exception e) {
                log.error("Failed to mark stuck transaction {} as failed", transaction.getId(), e);
            }
        }
    }
}

