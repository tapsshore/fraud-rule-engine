package za.co.capitecbank.fraud_rule_engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import za.co.capitecbank.fraud_rule_engine.dto.TransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Fraud Detection Integration Tests")
class FraudDetectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should process high amount transaction and flag it")
    void shouldProcessHighAmountTransactionAndFlagIt() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("INT-TXN-001")
                .accountId("INT-ACC-001")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.transaction.status").value("FLAGGED"))
                .andExpect(jsonPath("$.alerts").isArray())
                .andExpect(jsonPath("$.alerts[0].ruleName").value("HIGH_AMOUNT_RULE"));
    }

    @Test
    @DisplayName("Should process low amount transaction and approve it")
    void shouldProcessLowAmountTransactionAndApproveIt() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("INT-TXN-002")
                .accountId("INT-ACC-001")
                .amount(new BigDecimal("1000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .merchantCategory("RETAIL")
                .timestamp(LocalDateTime.now())
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(false))
                .andExpect(jsonPath("$.transaction.status").value("APPROVED"))
                .andExpect(jsonPath("$.alerts").isEmpty());
    }

    @Test
    @DisplayName("Should flag transaction with suspicious merchant")
    void shouldFlagTransactionWithSuspiciousMerchant() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("INT-TXN-003")
                .accountId("INT-ACC-002")
                .amount(new BigDecimal("5000.00"))
                .currency("ZAR")
                .transactionType("PURCHASE")
                .merchantCategory("GAMBLING")
                .timestamp(LocalDateTime.now())
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.alerts[0].ruleName").value("SUSPICIOUS_MERCHANT_RULE"));
    }

    @Test
    @DisplayName("Should flag cross-border transaction")
    void shouldFlagCrossBorderTransaction() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .transactionId("INT-TXN-004")
                .accountId("INT-ACC-003")
                .amount(new BigDecimal("5000.00"))
                .currency("USD")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.alerts[0].ruleName").value("CROSS_BORDER_RULE"));
    }

    @Test
    @DisplayName("Should return 400 for invalid transaction request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        // Given - missing required fields
        String invalidRequest = "{}";

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("Should return 404 for non-existent transaction")
    void shouldReturn404ForNonExistentTransaction() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/transactions/NON-EXISTENT-TXN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Actuator health endpoint should be accessible")
    void actuatorHealthShouldBeAccessible() throws Exception {
        // When & Then
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
