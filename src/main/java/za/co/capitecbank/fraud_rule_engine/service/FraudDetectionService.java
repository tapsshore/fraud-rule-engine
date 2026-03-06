package za.co.capitecbank.fraud_rule_engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;
import za.co.capitecbank.fraud_rule_engine.exception.DuplicateResourceException;
import za.co.capitecbank.fraud_rule_engine.repository.FraudAlertRepository;
import za.co.capitecbank.fraud_rule_engine.repository.TransactionRepository;
import za.co.capitecbank.fraud_rule_engine.rule.FraudRule;
import za.co.capitecbank.fraud_rule_engine.rule.RuleResult;


import java.util.ArrayList;
import java.util.List;

/**
 * Core service that orchestrates fraud detection by evaluating transactions
 * against all registered fraud rules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final List<FraudRule> fraudRules;

    /**
     * Processes a transaction through all enabled fraud rules.
     * Creates alerts for any rule violations and updates transaction status accordingly.
     *
     * @param transaction the transaction to process
     * @return FraudDetectionResult containing the processed transaction and any alerts
     */
    @Transactional
    public FraudDetectionResult processTransaction(Transaction transaction) {
        log.info("Processing transaction: {}", transaction.getTransactionId());

        // Check for duplicate transaction
        if (transactionRepository.findByTransactionId(transaction.getTransactionId()).isPresent()) {
            throw new DuplicateResourceException("Transaction", "transactionId", transaction.getTransactionId());
        }

        List<FraudAlert> alerts = evaluateRules(transaction);

        if (alerts.isEmpty()) {
            transaction.setStatus(TransactionStatus.APPROVED);
            Transaction savedTransaction = transactionRepository.save(transaction);
            log.info("Transaction {} approved - no fraud detected", transaction.getTransactionId());
            return FraudDetectionResult.approved(savedTransaction);
        }

        transaction.setStatus(TransactionStatus.FLAGGED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        List<FraudAlert> savedAlerts = saveAlerts(alerts, savedTransaction);

        log.warn("Transaction {} flagged with {} alert(s)",
                transaction.getTransactionId(), savedAlerts.size());

        return FraudDetectionResult.flagged(savedTransaction, savedAlerts);
    }

    private List<FraudAlert> evaluateRules(Transaction transaction) {
        List<FraudAlert> alerts = new ArrayList<>();

        for (FraudRule rule : fraudRules) {
            if (!rule.isEnabled()) {
                log.debug("Skipping disabled rule: {}", rule.getRuleName());
                continue;
            }

            rule.evaluate(transaction).ifPresent(result -> {
                FraudAlert alert = createAlert(transaction, result);
                alerts.add(alert);
                log.debug("Rule {} triggered for transaction {}",
                        result.ruleName(), transaction.getTransactionId());
            });
        }

        return alerts;
    }

    private FraudAlert createAlert(Transaction transaction, RuleResult result) {
        return FraudAlert.builder()
                .transaction(transaction)
                .ruleName(result.ruleName())
                .ruleDescription(result.description())
                .riskScore(result.riskScore())
                .build();
    }

    private List<FraudAlert> saveAlerts(List<FraudAlert> alerts, Transaction transaction) {
        List<FraudAlert> savedAlerts = new ArrayList<>();
        for (FraudAlert alert : alerts) {
            alert.setTransaction(transaction);
            savedAlerts.add(fraudAlertRepository.save(alert));
        }
        return savedAlerts;
    }
}
