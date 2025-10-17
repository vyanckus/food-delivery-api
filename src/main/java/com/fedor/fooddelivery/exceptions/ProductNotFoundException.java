package com.fedor.fooddelivery.exceptions;

/**
 * Исключение вызываемое когда товар не найден в базе данных
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Конструктор с идентификатором товара
     * @param productId идентификатор ненайденного товара
     */
    public ProductNotFoundException(Long productId) {
        super("Блюдо с ID " + productId + " не найдено");
    }
}
