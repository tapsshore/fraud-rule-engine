package za.co.capitecbank.fraud_rule_engine.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RuleResult Tests")
class RuleResultTest {

    @Test
    @DisplayName("Should create RuleResult with valid values")
    void shouldCreateRuleResultWithValidValues() {
        // When
        RuleResult result = RuleResult.of("TEST_RULE", "Test description", 75);

        // Then
        assertThat(result.getRuleName()).isEqualTo("TEST_RULE");
        assertThat(result.getDescription()).isEqualTo("Test description");
        assertThat(result.getRiskScore()).isEqualTo(75);
    }

    @Test
    @DisplayName("Should cap risk score at 100")
    void shouldCapRiskScoreAt100() {
        // When
        RuleResult result = RuleResult.of("TEST_RULE", "Test description", 150);

        // Then
        assertThat(result.getRiskScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should floor risk score at 0")
    void shouldFloorRiskScoreAt0() {
        // When
        RuleResult result = RuleResult.of("TEST_RULE", "Test description", -50);

        // Then
        assertThat(result.getRiskScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should accept boundary values")
    void shouldAcceptBoundaryValues() {
        // When
        RuleResult zeroResult = RuleResult.of("RULE_ZERO", "Zero score", 0);
        RuleResult hundredResult = RuleResult.of("RULE_HUNDRED", "Max score", 100);

        // Then
        assertThat(zeroResult.getRiskScore()).isEqualTo(0);
        assertThat(hundredResult.getRiskScore()).isEqualTo(100);
    }
}
