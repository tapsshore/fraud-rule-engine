package za.co.capitecbank.fraud_rule_engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.dto.FraudAlertResponse;
import za.co.capitecbank.fraud_rule_engine.exception.ResourceNotFoundException;
import za.co.capitecbank.fraud_rule_engine.repository.FraudAlertRepository;


import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class FraudAlertController {

    private final FraudAlertRepository fraudAlertRepository;

    @GetMapping
    public ResponseEntity<List<FraudAlertResponse>> getAllAlerts() {
        List<FraudAlertResponse> alerts = fraudAlertRepository.findAll().stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FraudAlertResponse> getAlertById(@PathVariable Long id) {
        FraudAlert alert = fraudAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FraudAlert", "id", id));
        return ResponseEntity.ok(FraudAlertResponse.fromEntity(alert));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByStatus(
            @PathVariable AlertStatus status) {
        List<FraudAlertResponse> alerts = fraudAlertRepository.findByAlertStatus(status).stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/high-risk")
    public ResponseEntity<List<FraudAlertResponse>> getHighRiskAlerts() {
        List<FraudAlertResponse> alerts = fraudAlertRepository.findByRiskScoreGreaterThanEqual(80).stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/rule/{ruleName}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByRule(
            @PathVariable String ruleName) {
        List<FraudAlertResponse> alerts = fraudAlertRepository.findByRuleName(ruleName).stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }
}
