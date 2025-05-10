package org.example.budgetmanager.Services;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.Transaction;
import org.example.budgetmanager.Repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactionsByCategoryId(Long categoryId) {
        return transactionRepository.findAllByCategoryId(categoryId);
    }

    public Optional<Transaction> getTransactionByCategoryIdAndId(Long categoryId, Long transactionId) {
        return transactionRepository.findTransactionByCategoryIdAndId(categoryId, transactionId);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

}
