package za.co.capitecbank.fraud_rule_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionResult;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudDetectionResponse {

    private TransactionResponse transaction;
    private List<FraudAlertResponse> alerts;
    private boolean flagged;
    private int totalRiskScore;

    public static FraudDetectionResponse fromResult(FraudDetectionResult result) {
        List<FraudAlertResponse> alertResponses = result.getAlerts().stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();

        int totalRisk = alertResponses.stream()
                .mapToInt(FraudAlertResponse::getRiskScore)
                .sum();

        return FraudDetectionResponse.builder()
                .transaction(TransactionResponse.fromEntity(result.getTransaction()))
                .alerts(alertResponses)
                .flagged(result.isFlagged())
                .totalRiskScore(Math.min(100, totalRisk))
                .build();
    }
}
