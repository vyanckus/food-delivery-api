package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO для запроса на создание заказа.
 * Содержит данные клиента и список товаров
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDto {
    private String customerName;
    private String phoneNumber;
    private List<OrderItemDto> items;

    /**
     * DTO для элемента заказа.
     * Содержит информацию о товаре и количестве
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;
    }
}
