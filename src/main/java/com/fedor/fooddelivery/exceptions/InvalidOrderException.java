package com.fedor.fooddelivery.exceptions;

/**
 * Исключение вызываемое при невалидных данных заказа
 * Например: пустое имя, неверный телефон, пустой заказ
 */
public class InvalidOrderException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке
     * @param message детальное описание ошибки валидации
     */
    public InvalidOrderException(String message) {
        super(message);
    }
}
