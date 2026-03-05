package za.co.capitecbank.fraudruleengine.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction Entity Tests")
class TransactionTest {

    @Test
    @DisplayName("Should create transaction with all required fields")
    void shouldCreateTransactionWithAllRequiredFields() {
        // Given
        String transactionId = "TXN-001";
        String accountId = "ACC-12345";
        BigDecimal amount = new BigDecimal("1500.00");
        String currency = "ZAR";
        String transactionType = "TRANSFER";
        String merchantName = "Test Merchant";
        String merchantCategory = "RETAIL";
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .accountId(accountId)
                .amount(amount)
                .currency(currency)
                .transactionType(transactionType)
                .merchantName(merchantName)
                .merchantCategory(merchantCategory)
                .timestamp(timestamp)
                .build();

        // Then
        assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
        assertThat(transaction.getAccountId()).isEqualTo(accountId);
        assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
        assertThat(transaction.getCurrency()).isEqualTo(currency);
        assertThat(transaction.getTransactionType()).isEqualTo(transactionType);
        assertThat(transaction.getMerchantName()).isEqualTo(merchantName);
        assertThat(transaction.getMerchantCategory()).isEqualTo(merchantCategory);
        assertThat(transaction.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("Should have default status as PENDING when created")
    void shouldHaveDefaultStatusAsPending() {
        // When
        Transaction transaction = Transaction.builder()
                .transactionId("TXN-002")
                .accountId("ACC-12345")
                .amount(new BigDecimal("500.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .timestamp(LocalDateTime.now())
                .build();

        // Then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
    }

    @Test
    @DisplayName("Should correctly compare two transactions with same transactionId")
    void shouldCorrectlyCompareTransactionsWithSameId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = Transaction.builder()
                .transactionId("TXN-003")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(now)
                .build();

        Transaction transaction2 = Transaction.builder()
                .transactionId("TXN-003")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(now)
                .build();

        // Then
        assertThat(transaction1).isEqualTo(transaction2);
        assertThat(transaction1.hashCode()).isEqualTo(transaction2.hashCode());
    }
}
