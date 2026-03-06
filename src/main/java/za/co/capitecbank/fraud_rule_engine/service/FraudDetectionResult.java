package za.co.capitecbank.fraud_rule_engine.service;



import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.util.List;

public record FraudDetectionResult(
        Transaction transaction,
        List<FraudAlert> alerts,
        boolean flagged
) {
    public static FraudDetectionResult flagged(Transaction transaction, List<FraudAlert> alerts) {
        return new FraudDetectionResult(transaction, alerts, true);
    }

    public static FraudDetectionResult approved(Transaction transaction) {
        return new FraudDetectionResult(transaction, List.of(), false);
    }
}
