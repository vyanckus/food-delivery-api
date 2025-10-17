package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для ответа на создание заказа.
 * Содержит статус операции и сообщение
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDto {
    private boolean success;
    private String message;

    /**
     * Конструктор для успешного ответа
     * @param success флаг успеха (true)
     */
    public OrderResponseDto(boolean success) {
        this.success = success;
        this.message = "You placed the order successfully. Thanks for using our services. Enjoy your food :)";
    }
}
