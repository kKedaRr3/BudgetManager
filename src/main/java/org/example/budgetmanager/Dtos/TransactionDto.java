package org.example.budgetmanager.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Double amount;
    private String description;
}
