package za.co.capitecbank.fraud_rule_engine.rule;

import za.co.capitecbank.fraud_rule_engine.domain.Transaction;

import java.util.Optional;

/**
 * Strategy interface for fraud detection rules.
 * Each implementation represents a specific fraud detection strategy.
 */
public interface FraudRule {

    /**
     * Evaluates a transaction against this fraud rule.
     *
     * @param transaction the transaction to evaluate
     * @return Optional containing RuleResult if fraud is detected, empty otherwise
     */
    Optional<RuleResult> evaluate(Transaction transaction);

    /**
     * Returns the unique name of this rule.
     */
    String getRuleName();

    /**
     * Returns whether this rule is currently enabled.
     */
    default boolean isEnabled() {
        return true;
    }
}
