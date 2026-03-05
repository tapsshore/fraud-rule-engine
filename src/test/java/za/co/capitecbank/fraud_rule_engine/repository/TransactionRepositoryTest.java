package za.co.capitecbank.fraud_rule_engine.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TransactionRepository Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        testTransaction = Transaction.builder()
                .transactionId("TXN-TEST-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1500.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .merchantName("Test Merchant")
                .merchantCategory("RETAIL")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve transaction")
    void shouldSaveAndRetrieveTransaction() {
        // When
        Transaction saved = transactionRepository.save(testTransaction);
        Optional<Transaction> found = transactionRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTransactionId()).isEqualTo("TXN-TEST-001");
        assertThat(found.get().getAmount()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Should find transaction by transactionId")
    void shouldFindTransactionByTransactionId() {
        // Given
        transactionRepository.save(testTransaction);

        // When
        Optional<Transaction> found = transactionRepository.findByTransactionId("TXN-TEST-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAccountId()).isEqualTo("ACC-12345");
    }

    @Test
    @DisplayName("Should find transactions by accountId")
    void shouldFindTransactionsByAccountId() {
        // Given
        transactionRepository.save(testTransaction);

        Transaction secondTransaction = Transaction.builder()
                .transactionId("TXN-TEST-002")
                .accountId("ACC-12345")
                .amount(new BigDecimal("2500.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(secondTransaction);

        // When
        List<Transaction> transactions = transactionRepository.findByAccountId("ACC-12345");

        // Then
        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("Should find transactions by status")
    void shouldFindTransactionsByStatus() {
        // Given
        transactionRepository.save(testTransaction);

        Transaction flaggedTransaction = Transaction.builder()
                .transactionId("TXN-TEST-003")
                .accountId("ACC-67890")
                .amount(new BigDecimal("50000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.FLAGGED)
                .build();
        transactionRepository.save(flaggedTransaction);

        // When
        List<Transaction> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        List<Transaction> flaggedTransactions = transactionRepository.findByStatus(TransactionStatus.FLAGGED);

        // Then
        assertThat(pendingTransactions).hasSize(1);
        assertThat(flaggedTransactions).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty when transaction not found")
    void shouldReturnEmptyWhenTransactionNotFound() {
        // When
        Optional<Transaction> found = transactionRepository.findByTransactionId("NON-EXISTENT");

        // Then
        assertThat(found).isEmpty();
    }
}
