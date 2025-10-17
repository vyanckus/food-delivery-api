package com.fedor.fooddelivery.repository;

import com.fedor.fooddelivery.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с товарами.
 * Наследует стандартные CRUD операции и добавляет кастомные методы
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Найти все товары по идентификатору категории
     * @param categoryId идентификатор категории
     * @return список товаров принадлежащих указанной категории
     */
    List<Product> findByCategoryId(Long categoryId);
}
