package za.co.capitecbank.fraud_rule_engine.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import za.co.capitecbank.fraud_rule_engine.dto.ApiError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/transactions");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Transaction", "id", "TXN-001");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleResourceNotFound(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("TXN-001");
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException")
    void shouldHandleDuplicateResourceException() {
        // Given
        DuplicateResourceException ex = new DuplicateResourceException("Transaction", "transactionId", "TXN-001");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleDuplicateResource(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).contains("TXN-001");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void shouldHandleValidationException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        FieldError fieldError = new FieldError("transactionRequest", "amount", -100, false, null, null, "Amount must be positive");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleValidationErrors(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors()).hasSize(1);
        assertThat(response.getBody().getFieldErrors().get(0).getField()).isEqualTo("amount");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument provided");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalArgument(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument provided");
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).contains("unexpected error");
    }
}
