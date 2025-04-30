package org.example.budgetmanager.Services;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;



}
