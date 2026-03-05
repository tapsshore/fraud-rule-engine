package za.co.capitecbank.fraud_rule_engine.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FraudAlertRepository Tests")
class FraudAlertRepositoryTest {

    @Autowired
    private FraudAlertRepository fraudAlertRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        fraudAlertRepository.deleteAll();
        transactionRepository.deleteAll();

        testTransaction = Transaction.builder()
                .transactionId("TXN-ALERT-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();
        testTransaction = transactionRepository.save(testTransaction);
    }

    @Test
    @DisplayName("Should save and retrieve fraud alert")
    void shouldSaveAndRetrieveFraudAlert() {
        // Given
        FraudAlert alert = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .ruleDescription("Amount exceeds R50,000")
                .riskScore(85)
                .build();

        // When
        FraudAlert saved = fraudAlertRepository.save(alert);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRuleName()).isEqualTo("HIGH_AMOUNT_RULE");
        assertThat(saved.getRiskScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("Should find alerts by transaction")
    void shouldFindAlertsByTransaction() {
        // Given
        FraudAlert alert1 = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(85)
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("VELOCITY_RULE")
                .riskScore(70)
                .build();

        fraudAlertRepository.save(alert1);
        fraudAlertRepository.save(alert2);

        // When
        List<FraudAlert> alerts = fraudAlertRepository.findByTransaction(testTransaction);

        // Then
        assertThat(alerts).hasSize(2);
    }

    @Test
    @DisplayName("Should find alerts by status")
    void shouldFindAlertsByStatus() {
        // Given
        FraudAlert pendingAlert = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(85)
                .alertStatus(AlertStatus.PENDING)
                .build();
        fraudAlertRepository.save(pendingAlert);

        // When
        List<FraudAlert> pendingAlerts = fraudAlertRepository.findByAlertStatus(AlertStatus.PENDING);

        // Then
        assertThat(pendingAlerts).hasSize(1);
        assertThat(pendingAlerts.get(0).getAlertStatus()).isEqualTo(AlertStatus.PENDING);
    }

    @Test
    @DisplayName("Should find alerts by rule name")
    void shouldFindAlertsByRuleName() {
        // Given
        FraudAlert alert = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("SUSPICIOUS_MERCHANT_RULE")
                .riskScore(90)
                .build();
        fraudAlertRepository.save(alert);

        // When
        List<FraudAlert> alerts = fraudAlertRepository.findByRuleName("SUSPICIOUS_MERCHANT_RULE");

        // Then
        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).getRuleName()).isEqualTo("SUSPICIOUS_MERCHANT_RULE");
    }

    @Test
    @DisplayName("Should find high risk alerts")
    void shouldFindHighRiskAlerts() {
        // Given
        FraudAlert lowRiskAlert = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("LOW_RISK_RULE")
                .riskScore(30)
                .build();

        FraudAlert highRiskAlert = FraudAlert.builder()
                .transaction(testTransaction)
                .ruleName("HIGH_RISK_RULE")
                .riskScore(90)
                .build();

        fraudAlertRepository.save(lowRiskAlert);
        fraudAlertRepository.save(highRiskAlert);

        // When
        List<FraudAlert> highRiskAlerts = fraudAlertRepository.findByRiskScoreGreaterThanEqual(80);

        // Then
        assertThat(highRiskAlerts).hasSize(1);
        assertThat(highRiskAlerts.get(0).getRiskScore()).isEqualTo(90);
    }
}
