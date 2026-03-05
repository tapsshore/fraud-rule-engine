package za.co.capitecbank.fraud_rule_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private String transactionId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String transactionType;
    private String merchantName;
    private String merchantCategory;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .transactionType(transaction.getTransactionType())
                .merchantName(transaction.getMerchantName())
                .merchantCategory(transaction.getMerchantCategory())
                .timestamp(transaction.getTimestamp())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
