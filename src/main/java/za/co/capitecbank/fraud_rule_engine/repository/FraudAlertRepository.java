package za.co.capitecbank.fraud_rule_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.capitecbank.fraud_rule_engine.domain.AlertStatus;
import za.co.capitecbank.fraud_rule_engine.domain.FraudAlert;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;


import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    List<FraudAlert> findByTransaction(Transaction transaction);

    List<FraudAlert> findByAlertStatus(AlertStatus alertStatus);

    List<FraudAlert> findByRuleName(String ruleName);

    List<FraudAlert> findByRiskScoreGreaterThanEqual(int riskScore);
}
