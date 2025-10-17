package com.fedor.fooddelivery.mapper;

import com.fedor.fooddelivery.dto.ProductDto;
import com.fedor.fooddelivery.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования сущности Product в ProductDto.
 * Содержит полную информацию о продукте
 */
@Component
public class ProductMapper {

    private static final Logger log = LoggerFactory.getLogger(ProductMapper.class);

    /**
     * Преобразовать сущность Product в ProductDto
     * @param product сущность продукта
     * @return DTO с полной информацией о продукте
     */
    public ProductDto toProductDto(Product product) {
        log.debug("Маппинг Product в ProductDto: {}", product.getName());

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setUrl(product.getUrl());
        dto.setCurrency(product.getCurrency());

        log.trace("ProductDto создан: id={}, name={}}", dto.getId(), dto.getName());
        return dto;
    }
}
