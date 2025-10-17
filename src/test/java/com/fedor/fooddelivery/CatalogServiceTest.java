package com.fedor.fooddelivery;

import com.fedor.fooddelivery.dto.CatalogProductDto;
import com.fedor.fooddelivery.dto.CatalogResponseDto;
import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.entity.Category;
import com.fedor.fooddelivery.entity.Product;
import com.fedor.fooddelivery.exceptions.CategoryNotFoundException;
import com.fedor.fooddelivery.mapper.CatalogProductMapper;
import com.fedor.fooddelivery.mapper.CategoryMapper;
import com.fedor.fooddelivery.repository.CategoryRepository;
import com.fedor.fooddelivery.repository.ProductRepository;
import com.fedor.fooddelivery.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Catalog Service Test")
class CatalogServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CatalogProductMapper catalogProductMapper;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    @DisplayName("Should return all categories when categories exist")
    void shouldReturnAllCategories_WhenCategoriesExist() {
        // given
        Category category1 = createCategory(1L, "Шаверма");
        Category category2 = createCategory(2L, "Салаты");
        List<Category> categories = List.of(category1, category2);

        CategoryDto categoryDto1 = createCategoryDto(1L, "Шаверма");
        CategoryDto categoryDto2 = createCategoryDto(2L, "Салаты");
        List<CategoryDto> categoriesDto = List.of(categoryDto1, categoryDto2);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toCategoryDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toCategoryDto(category2)).thenReturn(categoryDto2);

        // when
        List<CategoryDto> result = catalogService.getAllCategories();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categoriesDto, result);

        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toCategoryDto(category1);
        verify(categoryMapper, times(1)).toCategoryDto(category2);
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void shouldReturnEmptyList_WhenNoCategoriesExist() {
        // given
        when(categoryRepository.findAll()).thenReturn(List.of());

        // when
        List<CategoryDto> result = catalogService.getAllCategories();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, never()).toCategoryDto(any());
    }

    @Test
    @DisplayName("Should return products by category when category exists")
    void shouldReturnProductsByCategory_WhenCategoryExists() {
        // given
        Long categoryId = 1L;
        Category category = createCategory(categoryId, "Шаверма");
        CategoryDto categoryDto = createCategoryDto(categoryId, "Шаверма");

        Product product1 = createProduct(12L, "Шаверма Классическая", category);
        Product product2 = createProduct(13L, "Шаверма Сырная", category);
        List<Product> products = List.of(product1, product2);

        CatalogProductDto productDto1 = createCatalogProductDto(12L, "Шаверма Классическая");
        CatalogProductDto productDto2 = createCatalogProductDto(13L, "Шаверма Сырная");
        List<CatalogProductDto> productsDto = List.of(productDto1, productDto2);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);
        when(catalogProductMapper.toCatalogDto(product1)).thenReturn(productDto1);
        when(catalogProductMapper.toCatalogDto(product2)).thenReturn(productDto2);

        // when
        CatalogResponseDto result = catalogService.getProductsByCategory(categoryId);

        // then
        assertNotNull(result);
        assertEquals(categoryDto, result.getCategory());
        assertEquals(productsDto, result.getProducts());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toCategoryDto(category);
        verify(productRepository, times(1)).findByCategoryId(categoryId);
        verify(catalogProductMapper, times(1)).toCatalogDto(product1);
        verify(catalogProductMapper, times(1)).toCatalogDto(product2);
    }

    @Test
    @DisplayName("Should return empty products list when category exists but has no product")
    void shouldReturnEmptyProducts_WhenCategoryHasNoProducts() {
        // given
        Long categoryId = 1L;
        Category category = createCategory(categoryId, "Шаверма");
        CategoryDto categoryDto = createCategoryDto(categoryId, "Шаверма");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of());

        // when
        CatalogResponseDto result = catalogService.getProductsByCategory(categoryId);

        // then
        assertNotNull(result);
        assertEquals(categoryDto, result.getCategory());
        assertTrue(result.getProducts().isEmpty());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toCategoryDto(category);
        verify(productRepository, times(1)).findByCategoryId(categoryId);
        verify(catalogProductMapper, never()).toCatalogDto(any());
    }

    @ParameterizedTest
    @MethodSource("invalidCategoryIdsProvider")
    @DisplayName("Should throw exception when category not found")
    void shouldThrowException_WhenCategoryNotFound(Long categoryId) {
        // given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> catalogService.getProductsByCategory(categoryId));
        assertEquals("Категория с ID " + categoryId + " не найдена", exception.getMessage());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toCategoryDto(any());
        verify(productRepository, never()).findByCategoryId(any());
        verify(catalogProductMapper, never()).toCatalogDto(any());
    }

    @Test
    @DisplayName("Should map all product fields correctly when getting products by category")
    void shouldMapAllProductFields_WhenGettingProductsByCategory() {
        // given
        Long categoryId = 1L;
        Category category = createCategory(categoryId, "Шаверма");
        CategoryDto categoryDto = createCategoryDto(categoryId, "Шаверма");

        Product product = createProduct(12L, "Шаверма Классическая", category);
        CatalogProductDto expectedDto = createCatalogProductDto(12L, "Шаверма Классическая");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(product));
        when(catalogProductMapper.toCatalogDto(product)).thenReturn(expectedDto);

        // when
        CatalogResponseDto result = catalogService.getProductsByCategory(categoryId);

        // then
        assertNotNull(result);
        assertEquals(categoryDto, result.getCategory());
        assertEquals(1, result.getProducts().size());

        CatalogProductDto actualProductDto = result.getProducts().get(0);
        assertEquals(expectedDto.getId(), actualProductDto.getId());
        assertEquals(expectedDto.getName(), actualProductDto.getName());
        assertEquals(expectedDto.getPrice(), actualProductDto.getPrice());
        assertEquals(expectedDto.getUrl(), actualProductDto.getUrl());
        assertEquals(expectedDto.getCurrency(), actualProductDto.getCurrency());

        verify(catalogProductMapper, times(1)).toCatalogDto(product);
    }

    private static Stream<Arguments> invalidCategoryIdsProvider() {
        return Stream.of(
                Arguments.of(999L),
                Arguments.of(0L),
                Arguments.of(-1L),
                Arguments.of((Long) null)
        );
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setUrl("https://drive.google.com");
        return category;
    }

    private CategoryDto createCategoryDto(Long id, String name) {
        CategoryDto dto = new CategoryDto();
        dto.setId(id);
        dto.setName(name);
        dto.setUrl("https://drive.google.com");
        return dto;
    }

    private Product createProduct(Long id, String name, Category category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Описание");
        product.setPrice(250.0);
        product.setCategory(category);
        product.setUrl("https://drive.google.com");
        product.setCurrency("RUB");
        return product;
    }

    private CatalogProductDto createCatalogProductDto(Long id, String name) {
        CatalogProductDto dto = new CatalogProductDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(250.0);
        dto.setUrl("https://drive.google.com");
        dto.setCurrency("RUB");
        return dto;
    }
}
