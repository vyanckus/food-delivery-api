package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для отображения продукта в каталоге.
 * Содержит упрощенную информацию о продукте для списков
 */
@Getter
@Setter
@NoArgsConstructor
public class CatalogProductDto {
    private Long id;
    private String name;
    private Double price;
    private String url;
    private String currency;
}
