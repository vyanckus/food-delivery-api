package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO ответа для запроса товаров по категории.
 * Содержит информацию о категории и список товаров в ней
 */
@Getter
@Setter
@NoArgsConstructor
public class CatalogResponseDto {
    private CategoryDto category;
    private List<CatalogProductDto> products;
}
