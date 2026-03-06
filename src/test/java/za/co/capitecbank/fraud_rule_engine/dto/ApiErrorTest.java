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
        assertThat(error.status()).isEqualTo(404);
        assertThat(error.error()).isEqualTo("Not Found");
        assertThat(error.message()).isEqualTo("Resource not found");
        assertThat(error.path()).isEqualTo("/api/v1/transactions/123");
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.fieldErrors()).isNull();
    }

    @Test
    @DisplayName("Should create ApiError with field errors")
    void shouldCreateApiErrorWithFieldErrors() {
        // Given
        List<ApiError.FieldError> fieldErrors = List.of(
                new ApiError.FieldError("amount", "Amount must be positive", -100),
                new ApiError.FieldError("currency", "Currency is required", null)
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
        assertThat(error.status()).isEqualTo(400);
        assertThat(error.fieldErrors()).hasSize(2);
        assertThat(error.fieldErrors().get(0).field()).isEqualTo("amount");
        assertThat(error.fieldErrors().get(0).message()).isEqualTo("Amount must be positive");
        assertThat(error.fieldErrors().get(1).field()).isEqualTo("currency");
    }

    @Test
    @DisplayName("Should create FieldError with all fields")
    void shouldCreateFieldError() {
        // When
        ApiError.FieldError fieldError = new ApiError.FieldError(
                "transactionId",
                "Transaction ID is required",
                "");

        // Then
        assertThat(fieldError.field()).isEqualTo("transactionId");
        assertThat(fieldError.message()).isEqualTo("Transaction ID is required");
        assertThat(fieldError.rejectedValue()).isEqualTo("");
    }
}
