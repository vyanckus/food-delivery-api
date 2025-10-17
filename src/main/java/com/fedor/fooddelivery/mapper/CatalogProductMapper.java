package com.fedor.fooddelivery.mapper;

import com.fedor.fooddelivery.dto.CatalogProductDto;
import com.fedor.fooddelivery.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования сущности Product в CatalogProductDto
 */
@Component
public class CatalogProductMapper {

    private static final Logger log = LoggerFactory.getLogger(CatalogProductMapper.class);

    /**
     * Преобразовать сущность Product в CatalogProductDto
     * @param product сущность продукта
     * @return DTO продукта для каталога
     */
    public CatalogProductDto toCatalogDto(Product product) {
        log.debug("Маппинг Product в CatalogProductDto: {}", product.getName());

        CatalogProductDto dto = new CatalogProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setUrl(product.getUrl());
        dto.setCurrency(product.getCurrency());

        log.trace("CatalogProductDto создан: id={}, name={}", dto.getId(), dto.getName());
        return dto;
    }
}
