package za.co.capitecbank.fraud_rule_engine.dto;



import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String transactionId,
        String accountId,
        BigDecimal amount,
        String currency,
        String transactionType,
        String merchantName,
        String merchantCategory,
        LocalDateTime timestamp,
        TransactionStatus status,
        LocalDateTime createdAt
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getMerchantName(),
                transaction.getMerchantCategory(),
                transaction.getTimestamp(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}
