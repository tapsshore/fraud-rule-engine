package za.co.capitecbank.fraud_rule_engine.rule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.util.Optional;
import java.util.Set;

/**
 * Fraud rule that flags cross-border transactions (non-local currency).
 */
@Component
public class CrossBorderRule implements FraudRule {

    private static final String RULE_NAME = "CROSS_BORDER_RULE";

    private final Set<String> localCurrencies;

    public CrossBorderRule(@Value("${fraud.rules.cross-border.local-currencies:ZAR}") String localCurrenciesConfig) {
        this.localCurrencies = Set.of(localCurrenciesConfig.split(","));
    }

    public CrossBorderRule() {
        this.localCurrencies = Set.of("ZAR");
    }

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        String currency = transaction.getCurrency();

        if (currency == null || localCurrencies.contains(currency.toUpperCase())) {
            return Optional.empty();
        }

        String description = String.format(
                "Cross-border transaction detected with currency: %s",
                currency
        );

        int riskScore = calculateRiskScore(currency, transaction);
        return Optional.of(RuleResult.of(RULE_NAME, description, riskScore));
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    private int calculateRiskScore(String currency, Transaction transaction) {
        // Higher risk for high-risk currencies
        Set<String> highRiskCurrencies = Set.of("BTC", "ETH", "USDT");

        if (highRiskCurrencies.contains(currency.toUpperCase())) {
            return 85;
        }

        // Base score of 60 for foreign currency, higher for large amounts
        int baseScore = 60;
        if (transaction.getAmount().intValue() > 10000) {
            baseScore += 15;
        }

        return baseScore;
    }
}
