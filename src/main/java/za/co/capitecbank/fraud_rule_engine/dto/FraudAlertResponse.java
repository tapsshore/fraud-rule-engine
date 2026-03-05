package za.co.capitecbank.fraud_rule_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertResponse {

    private Long id;
    private String transactionId;
    private String ruleName;
    private String ruleDescription;
    private int riskScore;
    private AlertStatus alertStatus;
    private LocalDateTime createdAt;
    private String reviewedBy;
    private String reviewNotes;

    public static FraudAlertResponse fromEntity(FraudAlert alert) {
        return FraudAlertResponse.builder()
                .id(alert.getId())
                .transactionId(alert.getTransaction().getTransactionId())
                .ruleName(alert.getRuleName())
                .ruleDescription(alert.getRuleDescription())
                .riskScore(alert.getRiskScore())
                .alertStatus(alert.getAlertStatus())
                .createdAt(alert.getCreatedAt())
                .reviewedBy(alert.getReviewedBy())
                .reviewNotes(alert.getReviewNotes())
                .build();
    }
}
