package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи полной информации о продукте.
 * Используется для детального просмотра продукта
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
    private String url;
    private String currency;
}
