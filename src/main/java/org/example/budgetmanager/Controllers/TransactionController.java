package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.Transaction;
import org.example.budgetmanager.Repositories.TransactionRepository;
import org.example.budgetmanager.Services.TransactionService;
import org.example.budgetmanager.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{userId}/{categoryId}")
    public ResponseEntity<Iterable<Transaction>> getTransaction(@PathVariable String userId, @PathVariable String categoryId) {

        return null;
    }

}
