package za.co.capitecbank.fraud_rule_engine.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionResult;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FraudDetectionResponse DTO Tests")
class FraudDetectionResponseTest {

    @Test
    @DisplayName("Should convert FraudDetectionResult to response when flagged")
    void shouldConvertFlaggedResult() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("100000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.FLAGGED)
                .build();

        FraudAlert alert1 = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(80)
                .alertStatus(AlertStatus.PENDING)
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(2L)
                .transaction(transaction)
                .ruleName("CROSS_BORDER_RULE")
                .riskScore(60)
                .alertStatus(AlertStatus.PENDING)
                .build();

        FraudDetectionResult result = FraudDetectionResult.flagged(transaction, List.of(alert1, alert2));

        // When
        FraudDetectionResponse response = FraudDetectionResponse.fromResult(result);

        // Then
        assertThat(response.flagged()).isTrue();
        assertThat(response.alerts()).hasSize(2);
        assertThat(response.totalRiskScore()).isEqualTo(100); // Capped at 100
        assertThat(response.transaction().transactionId()).isEqualTo("TXN-001");
    }

    @Test
    @DisplayName("Should convert FraudDetectionResult to response when approved")
    void shouldConvertApprovedResult() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-002")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.APPROVED)
                .build();

        FraudDetectionResult result = FraudDetectionResult.approved(transaction);

        // When
        FraudDetectionResponse response = FraudDetectionResponse.fromResult(result);

        // Then
        assertThat(response.flagged()).isFalse();
        assertThat(response.alerts()).isEmpty();
        assertThat(response.totalRiskScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should cap total risk score at 100")
    void shouldCapTotalRiskScoreAt100() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-003")
                .accountId("ACC-12345")
                .amount(new BigDecimal("500000.00"))
                .currency("USD")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.FLAGGED)
                .build();

        FraudAlert alert1 = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("RULE_1")
                .riskScore(90)
                .alertStatus(AlertStatus.PENDING)
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(2L)
                .transaction(transaction)
                .ruleName("RULE_2")
                .riskScore(85)
                .alertStatus(AlertStatus.PENDING)
                .build();

        FraudDetectionResult result = FraudDetectionResult.flagged(transaction, List.of(alert1, alert2));

        // When
        FraudDetectionResponse response = FraudDetectionResponse.fromResult(result);

        // Then
        assertThat(response.totalRiskScore()).isEqualTo(100); // 90 + 85 = 175, capped at 100
    }
}
