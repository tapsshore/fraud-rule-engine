package za.co.capitecbank.fraud_rule_engine.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FraudAlertResponse DTO Tests")
class FraudAlertResponseTest {

    @Test
    @DisplayName("Should convert FraudAlert entity to FraudAlertResponse")
    void shouldConvertEntityToResponse() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .ruleDescription("Transaction exceeds threshold")
                .riskScore(85)
                .alertStatus(AlertStatus.PENDING)
                .reviewedBy("analyst@bank.com")
                .reviewNotes("Under review")
                .build();

        // When
        FraudAlertResponse response = FraudAlertResponse.fromEntity(alert);

        // Then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.transactionId()).isEqualTo("TXN-001");
        assertThat(response.ruleName()).isEqualTo("HIGH_AMOUNT_RULE");
        assertThat(response.ruleDescription()).isEqualTo("Transaction exceeds threshold");
        assertThat(response.riskScore()).isEqualTo(85);
        assertThat(response.alertStatus()).isEqualTo(AlertStatus.PENDING);
        assertThat(response.reviewedBy()).isEqualTo("analyst@bank.com");
        assertThat(response.reviewNotes()).isEqualTo("Under review");
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Given
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-002")
                .accountId("ACC-12345")
                .amount(new BigDecimal("50000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        FraudAlert alert = FraudAlert.builder()
                .id(2L)
                .transaction(transaction)
                .ruleName("SUSPICIOUS_MERCHANT_RULE")
                .riskScore(70)
                .alertStatus(AlertStatus.PENDING)
                .build();

        // When
        FraudAlertResponse response = FraudAlertResponse.fromEntity(alert);

        // Then
        assertThat(response.ruleDescription()).isNull();
        assertThat(response.reviewedBy()).isNull();
        assertThat(response.reviewNotes()).isNull();
    }
}
