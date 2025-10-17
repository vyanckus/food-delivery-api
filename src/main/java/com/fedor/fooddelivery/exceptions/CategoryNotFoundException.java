package com.fedor.fooddelivery.exceptions;

/**
 * Исключение вызываемое, когда категория не найдена в базе данных
 */
public class CategoryNotFoundException extends RuntimeException{

    /**
     * Конструктор с идентификатором категории
     * @param categoryId идентификатор ненайденной категории
     */
    public CategoryNotFoundException(Long categoryId) {
        super("Категория с ID " + categoryId + " не найдена");
    }
}
