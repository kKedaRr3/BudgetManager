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
    private AppUser user;

}
