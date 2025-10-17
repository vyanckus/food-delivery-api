package com.fedor.fooddelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи данных о категории.
 * Содержит базовую информацию о категории без связанных продуктов
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String url;
}
