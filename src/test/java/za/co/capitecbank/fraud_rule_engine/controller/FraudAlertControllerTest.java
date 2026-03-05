package za.co.capitecbank.fraud_rule_engine.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.repository.FraudAlertRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FraudAlertController.class)
@DisplayName("FraudAlertController Tests")
class FraudAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FraudAlertRepository fraudAlertRepository;

    private Transaction createTestTransaction() {
        return Transaction.builder()
                .id(1L)
                .transactionId("TXN-001")
                .accountId("ACC-12345")
                .amount(new BigDecimal("75000.00"))
                .currency("ZAR")
                .transactionType("TRANSFER")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/alerts - Should return all alerts")
    void shouldReturnAllAlerts() throws Exception {
        // Given
        Transaction transaction = createTestTransaction();
        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(80)
                .alertStatus(AlertStatus.PENDING)
                .build();

        when(fraudAlertRepository.findAll()).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ruleName").value("HIGH_AMOUNT_RULE"));
    }

    @Test
    @DisplayName("GET /api/v1/alerts/{id} - Should return alert by id")
    void shouldReturnAlertById() throws Exception {
        // Given
        Transaction transaction = createTestTransaction();
        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(80)
                .alertStatus(AlertStatus.PENDING)
                .build();

        when(fraudAlertRepository.findById(1L)).thenReturn(Optional.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ruleName").value("HIGH_AMOUNT_RULE"));
    }

    @Test
    @DisplayName("GET /api/v1/alerts/{id} - Should return 404 when not found")
    void shouldReturn404WhenAlertNotFound() throws Exception {
        // Given
        when(fraudAlertRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/alerts/status/{status} - Should return alerts by status")
    void shouldReturnAlertsByStatus() throws Exception {
        // Given
        Transaction transaction = createTestTransaction();
        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(80)
                .alertStatus(AlertStatus.PENDING)
                .build();

        when(fraudAlertRepository.findByAlertStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].alertStatus").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/v1/alerts/high-risk - Should return high risk alerts")
    void shouldReturnHighRiskAlerts() throws Exception {
        // Given
        Transaction transaction = createTestTransaction();
        FraudAlert alert = FraudAlert.builder()
                .id(1L)
                .transaction(transaction)
                .ruleName("HIGH_AMOUNT_RULE")
                .riskScore(90)
                .alertStatus(AlertStatus.PENDING)
                .build();

        when(fraudAlertRepository.findByRiskScoreGreaterThanEqual(80)).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/high-risk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].riskScore").value(90));
    }
}
