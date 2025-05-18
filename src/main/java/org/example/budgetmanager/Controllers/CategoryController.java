package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Services.CategoryService;
import org.example.budgetmanager.Dtos.CategoryDto;
import org.example.budgetmanager.Utils.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static org.example.budgetmanager.Utils.AuthUtils.getLoggedInUser;


@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final AuthUtils authUtils;

    @GetMapping("")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {

        Long userId = authUtils.getLoggedInUser().getId();

        var categories = categoryService.getAllCategoriesByUserId(userId).stream().map(category -> new CategoryDto(category.getId(), category.getName())).toList();

        if (categories.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long categoryId) {

        Long userId = authUtils.getLoggedInUser().getId();

        var category = categoryService.getCategoryByUserIdAndId(userId, categoryId).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        } else {
            CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
            return ResponseEntity.ok(categoryDto);
        }
    }

    @PostMapping("")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody Category category) {
        AppUser user = authUtils.getLoggedInUser();

        Category categoryToAdd = new Category();
        categoryToAdd.setName(category.getName());
        categoryToAdd.setUser(user);

        categoryService.save(categoryToAdd);
        CategoryDto categoryDto = new CategoryDto(categoryToAdd.getId(), categoryToAdd.getName());
        return ResponseEntity.ok(categoryDto);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {

        AppUser user = authUtils.getLoggedInUser();
        var categoryToUpdate = categoryService.getCategoryByUserIdAndId(user.getId(), categoryId).orElse(null);
        if (categoryToUpdate == null) {
            return ResponseEntity.notFound().build();
        }

        category.setId(categoryId);
        category.setUser(user);
        category.setTransactions(categoryToUpdate.getTransactions());
        categoryService.save(category);
        CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
        return ResponseEntity.ok(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryByUserIdAndId(authUtils.getLoggedInUser().getId(), categoryId).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        categoryService.delete(category);
        return ResponseEntity.noContent().build();
    }

}
