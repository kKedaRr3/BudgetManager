package org.example.budgetmanager.Services;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    public Optional<Category> getCategoryByUserIdAndId(Long userId, Long categoryId) {
        return categoryRepository.findCategoryByUserIdAndId(userId, categoryId);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }


    public void delete(Category category) {
        this.categoryRepository.delete(category);
    }
}
