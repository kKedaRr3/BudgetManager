package org.example.budgetmanager.Services;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;



}
