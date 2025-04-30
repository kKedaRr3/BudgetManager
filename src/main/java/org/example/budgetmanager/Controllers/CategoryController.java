package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Services.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

//    @GetMapping("")
//    public List<Category> getAllCategories() {
//
//    }

}
