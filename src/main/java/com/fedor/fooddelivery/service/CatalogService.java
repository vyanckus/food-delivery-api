package com.fedor.fooddelivery.service;

import com.fedor.fooddelivery.dto.CatalogProductDto;
import com.fedor.fooddelivery.dto.CatalogResponseDto;
import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.entity.Category;
import com.fedor.fooddelivery.exceptions.CategoryNotFoundException;
import com.fedor.fooddelivery.mapper.CatalogProductMapper;
import com.fedor.fooddelivery.mapper.CategoryMapper;
import com.fedor.fooddelivery.repository.CategoryRepository;
import com.fedor.fooddelivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с каталогом товаров и категорий
 */
@Service
@RequiredArgsConstructor
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final CatalogProductMapper catalogProductMapper;

    /**
     * Получить все категории товаров
     * @return список DTO категорий
     */
    public List<CategoryDto> getAllCategories() {
        log.info("Запрос на получение всех категорий");
        List<CategoryDto> categories = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
        log.debug("Найдено {} категорий", categories.size());
        return categories;
    }

    /**
     * Получить товары по идентификатору категории
     * @param categoryId идентификатор категории
     * @return DTO ответа с категорией и списком товаров
     * @throws CategoryNotFoundException если категория не найдена
     */
    public CatalogResponseDto getProductsByCategory(Long categoryId) {
        log.info("Запрос на получение товаров для категории ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Категория с ID {} не найдена", categoryId);
                    return new CategoryNotFoundException(categoryId);
                });

        log.debug("Найдена категория: {}", category.getName());

        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        List<CatalogProductDto> catalogProductsDto = productRepository.findByCategoryId(categoryId)
                .stream()
                .map(catalogProductMapper::toCatalogDto)
                .toList();

        log.debug("Найдено {} товаров в категории {}", catalogProductsDto.size(), category.getName());

        CatalogResponseDto catalogResponseDto = new CatalogResponseDto();
        catalogResponseDto.setCategory(categoryDto);
        catalogResponseDto.setProducts(catalogProductsDto);

        log.info("Успешно сформирован ответ для категории {}", category.getName());
        return catalogResponseDto;
    }
}
