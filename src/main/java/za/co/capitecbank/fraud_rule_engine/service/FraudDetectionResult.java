package za.co.capitecbank.fraud_rule_engine.service;

import lombok.Builder;
import lombok.Getter;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;


import java.util.List;

@Getter
@Builder
public class FraudDetectionResult {

    private final Transaction transaction;
    private final List<FraudAlert> alerts;
    private final boolean flagged;

    public static FraudDetectionResult flagged(Transaction transaction, List<FraudAlert> alerts) {
        return FraudDetectionResult.builder()
                .transaction(transaction)
                .alerts(alerts)
                .flagged(true)
                .build();
    }

    public static FraudDetectionResult approved(Transaction transaction) {
        return FraudDetectionResult.builder()
                .transaction(transaction)
                .alerts(List.of())
                .flagged(false)
                .build();
    }
}
