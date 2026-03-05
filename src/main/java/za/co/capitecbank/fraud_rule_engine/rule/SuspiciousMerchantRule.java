package za.co.capitecbank.fraud_rule_engine.rule;

import org.springframework.stereotype.Component;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.util.Optional;
import java.util.Set;

/**
 * Fraud rule that flags transactions from suspicious merchant categories.
 */
@Component
public class SuspiciousMerchantRule implements FraudRule {

    private static final String RULE_NAME = "SUSPICIOUS_MERCHANT_RULE";

    private static final Set<String> SUSPICIOUS_CATEGORIES = Set.of(
            "GAMBLING",
            "CRYPTOCURRENCY",
            "ADULT_ENTERTAINMENT",
            "MONEY_TRANSFER",
            "PRECIOUS_METALS"
    );

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        String merchantCategory = transaction.getMerchantCategory();

        if (merchantCategory == null || !SUSPICIOUS_CATEGORIES.contains(merchantCategory.toUpperCase())) {
            return Optional.empty();
        }

        String description = String.format(
                "Transaction at suspicious merchant category: %s",
                merchantCategory
        );

        int riskScore = calculateRiskScore(merchantCategory);
        return Optional.of(RuleResult.of(RULE_NAME, description, riskScore));
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    private int calculateRiskScore(String category) {
        return switch (category.toUpperCase()) {
            case "GAMBLING" -> 80;
            case "CRYPTOCURRENCY" -> 75;
            case "ADULT_ENTERTAINMENT" -> 60;
            case "MONEY_TRANSFER" -> 70;
            case "PRECIOUS_METALS" -> 65;
            default -> 50;
        };
    }
}
