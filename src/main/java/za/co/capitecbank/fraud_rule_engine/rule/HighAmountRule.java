package za.co.capitecbank.fraud_rule_engine.rule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Fraud rule that flags transactions exceeding a specified amount threshold.
 * Risk score increases proportionally with the amount.
 */
@Component
public class HighAmountRule implements FraudRule {

    private static final String RULE_NAME = "HIGH_AMOUNT_RULE";
    private final BigDecimal threshold;

    public HighAmountRule(@Value("${fraud.rules.high-amount.threshold:50000}") BigDecimal threshold) {
        this.threshold = threshold;
    }

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        if (transaction.getAmount().compareTo(threshold) <= 0) {
            return Optional.empty();
        }

        int riskScore = calculateRiskScore(transaction.getAmount());
        String description = String.format(
                "Transaction amount %s exceeds threshold of %s",
                transaction.getAmount(),
                threshold
        );

        return Optional.of(RuleResult.of(RULE_NAME, description, riskScore));
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    private int calculateRiskScore(BigDecimal amount) {
        // Base score of 70 for exceeding threshold
        // Additional points based on how much it exceeds
        BigDecimal ratio = amount.divide(threshold, 2, java.math.RoundingMode.HALF_UP);
        int additionalScore = ratio.intValue() * 5;
        return Math.min(100, 70 + additionalScore);
    }
}
