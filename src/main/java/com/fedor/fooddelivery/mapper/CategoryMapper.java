package com.fedor.fooddelivery.mapper;

import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования сущности Category в CategoryDto
 */
@Component
public class CategoryMapper {

    private static final Logger log = LoggerFactory.getLogger(CategoryMapper.class);

    /**
     * Преобразовать сущность Category в CategoryDto
     * @param category сущность категории
     * @return DTO категории
     */
    public CategoryDto toCategoryDto(Category category) {
        log.debug("Маппинг Category в CategoryDto: {}", category.getName());

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setUrl(category.getUrl());

        log.trace("CategoryDto создан: id={}, name={}", dto.getId(), dto.getName());
        return dto;
    }
}
