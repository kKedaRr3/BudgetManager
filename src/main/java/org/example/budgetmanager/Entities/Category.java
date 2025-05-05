package org.example.budgetmanager.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {

    private @Id
    @GeneratedValue Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @OneToMany
    @JoinColumn(name = "transaction_id")
    private List<Transaction> transactions;

}
