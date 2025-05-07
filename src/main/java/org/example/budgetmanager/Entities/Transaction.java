package org.example.budgetmanager.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

    private @Id
    @GeneratedValue Long id;

    @Column(nullable = false)
    private Double amount;

    private String description;

}
