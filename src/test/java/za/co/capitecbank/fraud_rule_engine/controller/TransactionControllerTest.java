package za.co.capitecbank.fraud_rule_engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;
import za.co.capitecbank.fraud_rule_engine.dto.TransactionRequest;
import za.co.capitecbank.fraud_rule_engine.repository.TransactionRepository;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionResult;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudDetectionService fraudDetectionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("POST /api/v1/transactions - Should process transaction and return result")
    void shouldProcessTransactionAndReturnResult() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .status(TransactionStatus.FLAGGED)
                .timestamp(LocalDateTime.now())
                .build();

        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(80)
                .build();

        FraudDetectionResult result = FraudDetectionResult.flagged(transaction, List.of(alert));
        when(fraudDetectionService.processTransaction(any(Transaction.class))).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.alerts").isArray())
                .andExpect(jsonPath("$.alerts[0].ruleName").value("HIGH_AMOUNT_RULE"));
    }

    @Test
    @DisplayName("POST /api/v1/transactions - Should return 400 for invalid request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        // Given - missing required fields
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("TXN-001")
                // missing accountId, amount, currency, transactionType
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/transactions/{transactionId} - Should return transaction")
    void shouldReturnTransaction() throws Exception {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .status(TransactionStatus.APPROVED)
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.findByTransactionId("TXN-001")).thenReturn(Optional.of(transaction));

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/TXN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN-001"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/{transactionId} - Should return 404 when not found")
    void shouldReturn404WhenTransactionNotFound() throws Exception {
        // Given
        when(transactionRepository.findByTransactionId("NON-EXISTENT")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/transactions - Should return all transactions")
    void shouldReturnAllTransactions() throws Exception {
        // Given
        Transaction tx1 = Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .status(TransactionStatus.APPROVED)
                .timestamp(LocalDateTime.now())
                .build();

        Transaction tx2 = Transaction.builder()
                .id(2L)
                .transactionId("TXN-002")
                .accountId("ACC-67890")
                .amount(new BigDecimal("50000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .status(TransactionStatus.FLAGGED)
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.findAll()).thenReturn(List.of(tx1, tx2));

        // When & Then
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
