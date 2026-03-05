package za.co.capitecbank.fraud_rule_engine.rule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RuleResult {

    private final String ruleName;
    private final String description;
    private final int riskScore;

    public static RuleResult of(String ruleName, String description, int riskScore) {
        return RuleResult.builder()
                .ruleName(ruleName)
                .description(description)
                .riskScore(Math.min(100, Math.max(0, riskScore)))
                .build();
    }
}
