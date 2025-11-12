package com.bank.fund.api;

import com.bank.fund.trading.application.SubscriptionApplicationService;
import com.bank.fund.trading.application.dto.SubscriptionRequest;
import com.bank.fund.trading.application.dto.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST API controller for fund subscription
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SubscriptionController {
    
    private final SubscriptionApplicationService subscriptionApplicationService;
    
    /**
     * Process fund subscription request
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Received subscription request: customerId={}, productCode={}, amount={}", 
                 request.getCustomerId(), request.getProductCode(), request.getAmount());
        
        SubscriptionResponse response = subscriptionApplicationService.processSubscription(request);
        
        if (response.isSuccess()) {
            log.info("Subscription succeeded: transactionSerialNumber={}", 
                     response.getTransactionSerialNumber());
            return ResponseEntity.ok(response);
        } else {
            log.error("Subscription failed: errorCode={}, errorMessage={}", 
                      response.getErrorCode(), response.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Query subscription transaction status
     */
    @GetMapping("/{transactionSerialNumber}")
    public ResponseEntity<String> queryTransaction(@PathVariable String transactionSerialNumber) {
        log.info("Querying transaction: {}", transactionSerialNumber);
        // Placeholder for query implementation
        return ResponseEntity.ok("Transaction query not yet implemented");
    }
}

