package za.co.capitecbank.fraud_rule_engine.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.dto.FraudDetectionResponse;
import za.co.capitecbank.fraud_rule_engine.dto.TransactionRequest;
import za.co.capitecbank.fraud_rule_engine.dto.TransactionResponse;
import za.co.capitecbank.fraud_rule_engine.exception.ResourceNotFoundException;
import za.co.capitecbank.fraud_rule_engine.repository.TransactionRepository;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionResult;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionService;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final FraudDetectionService fraudDetectionService;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<FraudDetectionResponse> processTransaction(
            @Valid @RequestBody TransactionRequest request) {

        log.info("Received transaction for processing: {}", request.getTransactionId());

        Transaction transaction = mapToEntity(request);
        FraudDetectionResult result = fraudDetectionService.processTransaction(transaction);

        return ResponseEntity.ok(FraudDetectionResponse.fromResult(result));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));
        return ResponseEntity.ok(TransactionResponse.fromEntity(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionRepository.findAll().stream()
                .map(TransactionResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(
            @PathVariable String accountId) {
        List<TransactionResponse> transactions = transactionRepository.findByAccountId(accountId).stream()
                .map(TransactionResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    private Transaction mapToEntity(TransactionRequest request) {
        return Transaction.builder()
                .transactionId(request.getTransactionId())
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .transactionType(request.getTransactionType())
                .merchantName(request.getMerchantName())
                .merchantCategory(request.getMerchantCategory())
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .build();
    }
}
