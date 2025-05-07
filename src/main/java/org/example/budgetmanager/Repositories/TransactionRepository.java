package org.example.budgetmanager.Repositories;

import org.example.budgetmanager.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByCategoryId(Long categoryId);

    Optional<Transaction> findTransactionByCategoryIdAndId(Long categoryId, Long transactionId);
}
