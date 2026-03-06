package za.co.capitecbank.fraud_rule_engine.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Custom Exception Tests")
class ExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException should format message with resource details")
    void resourceNotFoundExceptionShouldFormatMessage() {
        // When
        ResourceNotFoundException ex = new ResourceNotFoundException("Transaction", "transactionId", "TXN-001");

        // Then
        assertThat(ex.getMessage()).isEqualTo("Transaction not found with transactionId: 'TXN-001'");
    }

    @Test
    @DisplayName("ResourceNotFoundException should accept simple message")
    void resourceNotFoundExceptionShouldAcceptSimpleMessage() {
        // When
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Then
        assertThat(ex.getMessage()).isEqualTo("Resource not found");
    }

    @Test
    @DisplayName("DuplicateResourceException should format message with resource details")
    void duplicateResourceExceptionShouldFormatMessage() {
        // When
        DuplicateResourceException ex = new DuplicateResourceException("Transaction", "transactionId", "TXN-001");

        // Then
        assertThat(ex.getMessage()).isEqualTo("Transaction already exists with transactionId: 'TXN-001'");
    }

    @Test
    @DisplayName("DuplicateResourceException should accept simple message")
    void duplicateResourceExceptionShouldAcceptSimpleMessage() {
        // When
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate entry");

        // Then
        assertThat(ex.getMessage()).isEqualTo("Duplicate entry");
    }
}
