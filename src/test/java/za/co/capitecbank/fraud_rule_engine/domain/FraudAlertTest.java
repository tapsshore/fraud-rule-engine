package za.co.capitecbank.fraud_rule_engine.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FraudAlert Entity Tests")
class FraudAlertTest {

    @Test
    @DisplayName("Should create fraud alert with required fields")
    void shouldCreateFraudAlertWithRequiredFields() {
        // Given
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("50000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        String ruleName = "HIGH_AMOUNT_RULE";
        String ruleDescription = "Transaction amount exceeds threshold";
        int riskScore = 85;

        // When
        FraudAlert alert = FraudAlert.builder()
                .transaction(transaction)
                .ruleName(ruleName)
                .ruleDescription(ruleDescription)
                .riskScore(riskScore)
                .build();

        // Then
        assertThat(alert.getTransaction()).isEqualTo(transaction);
        assertThat(alert.getRuleName()).isEqualTo(ruleName);
        assertThat(alert.getRuleDescription()).isEqualTo(ruleDescription);
        assertThat(alert.getRiskScore()).isEqualTo(riskScore);
    }

    @Test
    @DisplayName("Should have default status as PENDING when created")
    void shouldHaveDefaultStatusAsPending() {
        // Given
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-002")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("WITHDRAWAL")
                .timestamp(LocalDateTime.now())
                .build();

        // When
        FraudAlert alert = FraudAlert.builder()
                .transaction(transaction)
                .ruleName("VELOCITY_RULE")
                .riskScore(70)
                .build();

        // Then
        assertThat(alert.getAlertStatus()).isEqualTo(AlertStatus.PENDING);
    }

    @Test
    @DisplayName("Should allow risk score between 0 and 100")
    void shouldAllowRiskScoreBetweenZeroAndHundred() {
        // Given
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-003")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .timestamp(LocalDateTime.now())
                .build();

        // When
        FraudAlert lowRiskAlert = FraudAlert.builder()
                .transaction(transaction)
                .ruleName("LOW_RISK_RULE")
                .riskScore(0)
                .build();

        FraudAlert highRiskAlert = FraudAlert.builder()
                .transaction(transaction)
                .ruleName("HIGH_RISK_RULE")
                .riskScore(100)
                .build();

        // Then
        assertThat(lowRiskAlert.getRiskScore()).isEqualTo(0);
        assertThat(highRiskAlert.getRiskScore()).isEqualTo(100);
    }
}
