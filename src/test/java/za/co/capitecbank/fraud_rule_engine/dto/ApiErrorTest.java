package za.co.capitecbank.fraud_rule_engine.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiError DTO Tests")
class ApiErrorTest {

    @Test
    @DisplayName("Should create ApiError with of() factory method")
    void shouldCreateApiErrorWithOfMethod() {
        // When
        ApiError error = ApiError.of(404, "Not Found", "Resource not found", "/api/v1/transactions/123");

        // Then
        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getError()).isEqualTo("Not Found");
        assertThat(error.getMessage()).isEqualTo("Resource not found");
        assertThat(error.getPath()).isEqualTo("/api/v1/transactions/123");
        assertThat(error.getTimestamp()).isNotNull();
        assertThat(error.getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("Should create ApiError with field errors")
    void shouldCreateApiErrorWithFieldErrors() {
        // Given
        List<ApiError.FieldError> fieldErrors = List.of(
                ApiError.FieldError.builder()
                        .field("amount")
                        .message("Amount must be positive")
                        .rejectedValue(-100)
                        .build(),
                ApiError.FieldError.builder()
                        .field("currency")
                        .message("Currency is required")
                        .rejectedValue(null)
                        .build()
        );

        // When
        ApiError error = ApiError.withFieldErrors(
                400,
                "Validation Failed",
                "One or more fields have validation errors",
                "/api/v1/transactions",
                fieldErrors
        );

        // Then
        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getFieldErrors()).hasSize(2);
        assertThat(error.getFieldErrors().get(0).getField()).isEqualTo("amount");
        assertThat(error.getFieldErrors().get(0).getMessage()).isEqualTo("Amount must be positive");
        assertThat(error.getFieldErrors().get(1).getField()).isEqualTo("currency");
    }

    @Test
    @DisplayName("Should create FieldError with all fields")
    void shouldCreateFieldError() {
        // When
        ApiError.FieldError fieldError = ApiError.FieldError.builder()
                .field("transactionId")
                .message("Transaction ID is required")
                .rejectedValue("")
                .build();

        // Then
        assertThat(fieldError.getField()).isEqualTo("transactionId");
        assertThat(fieldError.getMessage()).isEqualTo("Transaction ID is required");
        assertThat(fieldError.getRejectedValue()).isEqualTo("");
    }
}
