package org.example.budgetmanager.Repositories;

import org.example.budgetmanager.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

    Optional<Category> findCategoryByUserIdAndId(Long userId, Long categoryId);

}
