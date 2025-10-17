package com.fedor.fooddelivery.controller;

import com.fedor.fooddelivery.dto.CatalogResponseDto;
import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Контроллер для работы с каталогом товаров.
 * Обрабатывает HTTP запросы связанные с категориями и товарами
 */
@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;

    /**
     * Получить все категории товаров
     * GET /catalog
     *
     * @return коллекция DTO категорий
     */
    @GetMapping
    public Collection<CategoryDto> getAllCategories() {
        log.info("HTTP GET /catalog - запрос на получение всех категорий");

        Collection<CategoryDto> categories = catalogService.getAllCategories();

        log.debug("HTTP GET /catalog - возвращено {} категорий", categories.size());
        return categories;
    }

    /**
     * Получить товары по идентификатору категории
     * GET /catalog/{id}
     *
     * @param id идентификатор категории
     * @return DTO ответа с категорией и списком товаров
     */
    @GetMapping("/{id}")
    public CatalogResponseDto getProductsByCategory(@PathVariable Long id) {
        log.info("HTTP GET /catalog/{} - запрос товаров категории", id);

        CatalogResponseDto response = catalogService.getProductsByCategory(id);

        log.debug("HTTP GET /catalog/{} - возвращено {} товаров",
                id, response.getProducts() != null ? response.getProducts().size() : 0);
        return response;
    }
}
