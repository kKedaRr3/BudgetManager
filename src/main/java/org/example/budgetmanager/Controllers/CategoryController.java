package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Services.CategoryService;
import org.example.budgetmanager.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories() {

        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Long userId = userService.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("User not found")).getId();

        var categories = categoryService.getAllCategoriesByUserId(userId);
        if (categories.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {

        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Long userId = userService.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("User not found")).getId();

        var category = categoryService.getCategoryByUserIdAndId(userId, categoryId).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(category);
        }
    }

}
