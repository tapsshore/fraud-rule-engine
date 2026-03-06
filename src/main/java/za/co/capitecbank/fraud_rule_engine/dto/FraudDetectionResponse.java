package za.co.capitecbank.fraud_rule_engine.dto;


import za.co.capitecbank.fraud_rule_engine.service.FraudDetectionResult;

import java.util.List;

public record FraudDetectionResponse(
        TransactionResponse transaction,
        List<FraudAlertResponse> alerts,
        boolean flagged,
        int totalRiskScore
) {
    public static FraudDetectionResponse fromResult(FraudDetectionResult result) {
        List<FraudAlertResponse> alertResponses = result.alerts().stream()
                .map(FraudAlertResponse::fromEntity)
                .toList();

        int totalRisk = alertResponses.stream()
                .mapToInt(FraudAlertResponse::riskScore)
                .sum();

        return new FraudDetectionResponse(
                TransactionResponse.fromEntity(result.transaction()),
                alertResponses,
                result.flagged(),
                Math.min(100, totalRisk)
        );
    }
}
