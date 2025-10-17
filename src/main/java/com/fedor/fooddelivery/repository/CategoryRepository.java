package com.fedor.fooddelivery.repository;

import com.fedor.fooddelivery.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с категориями товаров.
 * Наследует все стандартные CRUD операции от JpaRepository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
