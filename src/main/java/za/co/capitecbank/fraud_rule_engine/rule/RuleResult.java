package za.co.capitecbank.fraud_rule_engine.rule;

public record RuleResult(
        String ruleName,
        String description,
        int riskScore
) {
    public static RuleResult of(String ruleName, String description, int riskScore) {
        // Ensure risk score is between 0 and 100
        int normalizedScore = Math.min(100, Math.max(0, riskScore));
        return new RuleResult(ruleName, description, normalizedScore);
    }
}
