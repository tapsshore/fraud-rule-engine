package za.co.capitecbank.fraud_rule_engine.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fraud Rule Tests")
class FraudRuleTest {

    @Nested
    @DisplayName("HighAmountRule Tests")
    class HighAmountRuleTest {

        private final HighAmountRule rule = new HighAmountRule(new BigDecimal("50000.00"));

        @Test
        @DisplayName("Should flag transaction exceeding threshold")
        void shouldFlagTransactionExceedingThreshold() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-001")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("75000.00"))
                    .currency("ZAR")
                    .transactionType("TRANSFER")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().ruleName()).isEqualTo("HIGH_AMOUNT_RULE");
            assertThat(result.get().riskScore()).isGreaterThanOrEqualTo(70);
        }

        @Test
        @DisplayName("Should not flag transaction below threshold")
        void shouldNotFlagTransactionBelowThreshold() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-002")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("25000.00"))
                    .currency("ZAR")
                    .transactionType("TRANSFER")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return higher risk score for much larger amounts")
        void shouldReturnHigherRiskScoreForLargerAmounts() {
            // Given
            Transaction largeTransaction = Transaction.builder()
                    .transactionId("TXN-003")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("500000.00"))
                    .currency("ZAR")
                    .transactionType("TRANSFER")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(largeTransaction);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().riskScore()).isGreaterThanOrEqualTo(90);
        }
    }

    @Nested
    @DisplayName("SuspiciousMerchantRule Tests")
    class SuspiciousMerchantRuleTest {

        private final SuspiciousMerchantRule rule = new SuspiciousMerchantRule();

        @Test
        @DisplayName("Should flag transaction with suspicious merchant category")
        void shouldFlagSuspiciousMerchantCategory() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-004")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("5000.00"))
                    .currency("ZAR")
                    .transactionType("PURCHASE")
                    .merchantCategory("GAMBLING")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().ruleName()).isEqualTo("SUSPICIOUS_MERCHANT_RULE");
        }

        @Test
        @DisplayName("Should not flag transaction with normal merchant category")
        void shouldNotFlagNormalMerchantCategory() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-005")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("500.00"))
                    .currency("ZAR")
                    .transactionType("PURCHASE")
                    .merchantCategory("RETAIL")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("CrossBorderRule Tests")
    class CrossBorderRuleTest {

        private final CrossBorderRule rule = new CrossBorderRule();

        @Test
        @DisplayName("Should flag transaction with foreign currency")
        void shouldFlagForeignCurrencyTransaction() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-006")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("10000.00"))
                    .currency("USD")
                    .transactionType("TRANSFER")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().ruleName()).isEqualTo("CROSS_BORDER_RULE");
        }

        @Test
        @DisplayName("Should not flag transaction with local currency")
        void shouldNotFlagLocalCurrencyTransaction() {
            // Given
            Transaction transaction = Transaction.builder()
                    .transactionId("TXN-007")
                    .accountId("ACC-12345")
                    .amount(new BigDecimal("10000.00"))
                    .currency("ZAR")
                    .transactionType("TRANSFER")
                    .timestamp(LocalDateTime.now())
                    .build();

            // When
            Optional<RuleResult> result = rule.evaluate(transaction);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
