package za.co.capitecbank.fraud_rule_engine.dto;



import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;

import java.time.LocalDateTime;

public record FraudAlertResponse(
        Long id,
        String transactionId,
        String ruleName,
        String ruleDescription,
        int riskScore,
        AlertStatus alertStatus,
        LocalDateTime createdAt,
        String reviewedBy,
        String reviewNotes
) {
    public static FraudAlertResponse fromEntity(FraudAlert alert) {
        return new FraudAlertResponse(
                alert.getId(),
                alert.getTransaction().getTransactionId(),
                alert.getRuleName(),
                alert.getRuleDescription(),
                alert.getRiskScore(),
                alert.getAlertStatus(),
                alert.getCreatedAt(),
                alert.getReviewedBy(),
                alert.getReviewNotes()
        );
    }
}
