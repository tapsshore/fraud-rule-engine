package za.co.capitecbank.fraud_rule_engine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;
import za.co.capitecbank.fraud_rule_engine.exception.DuplicateResourceException;
import za.co.capitecbank.fraud_rule_engine.repository.FraudAlertRepository;
import za.co.capitecbank.fraud_rule_engine.repository.TransactionRepository;
import za.co.capitecbank.fraud_rule_engine.rule.FraudRule;
import za.co.capitecbank.fraud_rule_engine.rule.RuleResult;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionService Tests")
class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private FraudRule mockRule1;

    @Mock
    private FraudRule mockRule2;

    private FraudDetectionService fraudDetectionService;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        List<FraudRule> rules = List.of(mockRule1, mockRule2);
        fraudDetectionService = new FraudDetectionService(
                transactionRepository,
                fraudAlertRepository,
                rules
        );

        testTransaction = Transaction.builder()
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        // Default: no duplicate exists
        when(transactionRepository.findByTransactionId(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Should process transaction and create alerts when rules trigger")
    void shouldProcessTransactionAndCreateAlerts() {
        // Given
        RuleResult ruleResult = RuleResult.of("TEST_RULE", "Test description", 80);
        when(mockRule1.isEnabled()).thenReturn(true);
        when(mockRule1.evaluate(any(Transaction.class))).thenReturn(Optional.of(ruleResult));
        when(mockRule2.isEnabled()).thenReturn(true);
        when(mockRule2.evaluate(any(Transaction.class))).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FraudDetectionResult result = fraudDetectionService.processTransaction(testTransaction);

        // Then
        assertThat(result.transaction()).isNotNull();
        assertThat(result.alerts()).hasSize(1);
        assertThat(result.flagged()).isTrue();
        verify(fraudAlertRepository, times(1)).save(any(FraudAlert.class));
    }

    @Test
    @DisplayName("Should mark transaction as APPROVED when no rules trigger")
    void shouldApproveTransactionWhenNoRulesTrigger() {
        // Given
        when(mockRule1.isEnabled()).thenReturn(true);
        when(mockRule1.evaluate(any(Transaction.class))).thenReturn(Optional.empty());
        when(mockRule2.isEnabled()).thenReturn(true);
        when(mockRule2.evaluate(any(Transaction.class))).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        FraudDetectionResult result = fraudDetectionService.processTransaction(testTransaction);

        // Then
        assertThat(result.flagged()).isFalse();
        assertThat(result.alerts()).isEmpty();
        verify(fraudAlertRepository, never()).save(any(FraudAlert.class));
    }

    @Test
    @DisplayName("Should skip disabled rules")
    void shouldSkipDisabledRules() {
        // Given
        RuleResult ruleResult = RuleResult.of("TEST_RULE", "Test description", 80);
        when(mockRule1.isEnabled()).thenReturn(false);
        when(mockRule2.isEnabled()).thenReturn(true);
        when(mockRule2.evaluate(any(Transaction.class))).thenReturn(Optional.of(ruleResult));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FraudDetectionResult result = fraudDetectionService.processTransaction(testTransaction);

        // Then
        assertThat(result.alerts()).hasSize(1);
        verify(mockRule1, never()).evaluate(any(Transaction.class));
        verify(mockRule2, times(1)).evaluate(any(Transaction.class));
    }

    @Test
    @DisplayName("Should process multiple rule violations")
    void shouldProcessMultipleRuleViolations() {
        // Given
        RuleResult result1 = RuleResult.of("RULE_1", "First rule triggered", 70);
        RuleResult result2 = RuleResult.of("RULE_2", "Second rule triggered", 85);
        when(mockRule1.isEnabled()).thenReturn(true);
        when(mockRule1.evaluate(any(Transaction.class))).thenReturn(Optional.of(result1));
        when(mockRule2.isEnabled()).thenReturn(true);
        when(mockRule2.evaluate(any(Transaction.class))).thenReturn(Optional.of(result2));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FraudDetectionResult result = fraudDetectionService.processTransaction(testTransaction);

        // Then
        assertThat(result.alerts()).hasSize(2);
        assertThat(result.flagged()).isTrue();
        verify(fraudAlertRepository, times(2)).save(any(FraudAlert.class));
    }

    @Test
    @DisplayName("Should update transaction status to FLAGGED when alerts are created")
    void shouldUpdateTransactionStatusToFlagged() {
        // Given
        RuleResult ruleResult = RuleResult.of("TEST_RULE", "Test description", 80);
        when(mockRule1.isEnabled()).thenReturn(true);
        when(mockRule1.evaluate(any(Transaction.class))).thenReturn(Optional.of(ruleResult));
        when(mockRule2.isEnabled()).thenReturn(true);
        when(mockRule2.evaluate(any(Transaction.class))).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.<Transaction>getArgument(0));
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FraudDetectionResult result = fraudDetectionService.processTransaction(testTransaction);

        // Then
        assertThat(result.flagged()).isTrue();
        verify(transactionRepository).save(argThat(tx ->
            tx.getStatus() == TransactionStatus.FLAGGED
        ));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when transaction already exists")
    void shouldThrowExceptionWhenTransactionAlreadyExists() {
        // Given
        when(transactionRepository.findByTransactionId("TXN-001")).thenReturn(Optional.of(testTransaction));

        // When & Then
        assertThatThrownBy(() -> fraudDetectionService.processTransaction(testTransaction))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("TXN-001");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
