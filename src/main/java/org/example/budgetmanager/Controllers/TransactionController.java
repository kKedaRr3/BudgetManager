package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Dtos.TransactionDto;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Entities.Transaction;
import org.example.budgetmanager.Services.CategoryService;
import org.example.budgetmanager.Services.TransactionService;
import org.example.budgetmanager.Services.UserService;
import org.example.budgetmanager.Utils.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    private final CategoryService categoryService;

    private final UserService userService;

    private final AuthUtils authUtils;

    @GetMapping("/{categoryId}")
    public ResponseEntity<Iterable<TransactionDto>> getAllTransactions(@PathVariable Long categoryId) {

        var transactions = transactionService.getAllTransactionsByCategoryId(categoryId).stream().map(transaction -> new TransactionDto(transaction.getId(), transaction.getAmount(), transaction.getDescription())).toList();
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(transactions);
        }
    }

    @GetMapping("/{categoryId}/{transactionId}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long categoryId, @PathVariable Long transactionId) {
        var transaction = transactionService.getTransactionByCategoryIdAndId(categoryId, transactionId);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            TransactionDto transactionDto = new TransactionDto(transaction.getId(), transaction.getAmount(), transaction.getDescription());
            return ResponseEntity.ok(transactionDto);
        }
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<TransactionDto> createTransaction(@PathVariable Long categoryId, @RequestBody Transaction transaction){

        Category category = getCategoryByCategoryId(categoryId);

        Transaction transactionToAdd = new Transaction();
        transactionToAdd.setAmount(transaction.getAmount());
        transactionToAdd.setDescription(transaction.getDescription());
        transactionToAdd.setCategory(category);
        transactionService.save(transactionToAdd);
        TransactionDto transactionDto = new TransactionDto(transactionToAdd.getId(), transactionToAdd.getAmount(), transactionToAdd.getDescription());
        return ResponseEntity.ok(transactionDto);
    }

    @PutMapping("/{categoryId}/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long categoryId, @PathVariable Long transactionId, @RequestBody Transaction transaction){
        var transactionToUpdate = transactionService.getTransactionByCategoryIdAndId(categoryId, transactionId);
        if (transactionToUpdate == null){
            return ResponseEntity.notFound().build();
        }
        transaction.setId(transactionId);
        transaction.setCategory(getCategoryByCategoryId(categoryId));
        transactionService.save(transaction);
        TransactionDto transactionDto = new TransactionDto(transaction.getId(), transaction.getAmount(), transaction.getDescription());
        return ResponseEntity.ok(transactionDto);
    }

    @DeleteMapping("/{categoryId}/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long categoryId, @PathVariable Long transactionId){
        var transaction = transactionService.getTransactionByCategoryIdAndId(categoryId, transactionId);
        if (transaction == null){
            return ResponseEntity.notFound().build();
        }
        transactionService.delete(transaction.getId());
        return ResponseEntity.noContent().build();
    }

    private Category getCategoryByCategoryId(Long categoryId){
        var user = authUtils.getLoggedInUser();
        return categoryService.getCategoryByUserIdAndId(user.getId(), categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
    }

}
