package za.co.capitecbank.fraud_rule_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.capitecbank.fraud_rule_engine.domain.Transaction;
import za.co.capitecbank.fraud_rule_engine.domain.TransactionStatus;


import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByAccountId(String accountId);

    List<Transaction> findByStatus(TransactionStatus status);
}
