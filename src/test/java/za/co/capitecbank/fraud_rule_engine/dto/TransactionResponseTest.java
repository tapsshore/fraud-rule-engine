package za.co.capitecbank.fraud_rule_engine.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransactionResponse DTO Tests")
class TransactionResponseTest {

    @Test
    @DisplayName("Should convert Transaction entity to TransactionResponse")
    void shouldConvertEntityToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("5000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .merchantName("Test Merchant")
                .merchantCategory("RETAIL")
                .timestamp(now)
                .status(TransactionStatus.APPROVED)
                .build();

        // When
        TransactionResponse response = TransactionResponse.fromEntity(transaction);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTransactionId()).isEqualTo("TXN-001");
        assertThat(response.getAccountId()).isEqualTo("ACC-12345");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(response.getCurrency()).isEqualTo("ZAR");
        assertThat(response.getTransactionType()).isEqualTo("TRANSFER");
        assertThat(response.getMerchantName()).isEqualTo("Test Merchant");
        assertThat(response.getMerchantCategory()).isEqualTo("RETAIL");
        assertThat(response.getTimestamp()).isEqualTo(now);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(2L)
                .transactionId("TXN-002")
                .accountId("ACC-67890")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.PENDING)
                .build();

        // When
        TransactionResponse response = TransactionResponse.fromEntity(transaction);

        // Then
        assertThat(response.getMerchantName()).isNull();
        assertThat(response.getMerchantCategory()).isNull();
    }
}
