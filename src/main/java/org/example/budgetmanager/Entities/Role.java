package org.example.budgetmanager.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
