package com.fedor.fooddelivery;

import com.fedor.fooddelivery.controller.CatalogController;
import com.fedor.fooddelivery.dto.CatalogProductDto;
import com.fedor.fooddelivery.dto.CatalogResponseDto;
import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.exceptions.CategoryNotFoundException;
import com.fedor.fooddelivery.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@DisplayName("Catalog Controller Test")
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Test
    @DisplayName("Should return all categories")
    void shouldReturnAllCategories() throws Exception {
        // given
        CategoryDto category1 = createCategoryDto(1L, "Шаверма");
        CategoryDto category2 = createCategoryDto(2L, "Салаты");
        List<CategoryDto> categories = List.of(category1, category2);

        when(catalogService.getAllCategories()).thenReturn(categories);

        // when & then
        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Шаверма"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Салаты"));
    }

    @Test
    @DisplayName("Should return products by category")
    void shouldReturnProductsByCategory() throws Exception {
        Long categoryId = 1L;
        CatalogResponseDto response = createCatalogResponse(categoryId);

        when(catalogService.getProductsByCategory(categoryId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/catalog/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category.id").value(1L))
                .andExpect(jsonPath("$.category.name").value("Шаверма"))
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.products[0].id").value(12L))
                .andExpect(jsonPath("$.products[0].name").value("Шаверма Классическая"))
                .andExpect(jsonPath("$.products[1].id").value(13L))
                .andExpect(jsonPath("$.products[1].name").value("Шаверма Сырная"));
    }

    @Test
    @DisplayName("Should return 404 when category not found")
    void shouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        Long categoryId = 999L;

        when(catalogService.getProductsByCategory(categoryId)).thenThrow(new CategoryNotFoundException(categoryId));

        // when & then
        mockMvc.perform(get("/catalog/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Категория с ID 999 не найдена"));
    }

    @Test
    @DisplayName("Should return empty categories list")
    void shouldReturnEmptyCategoriesList() throws Exception {
        // given
        when(catalogService.getAllCategories()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should return empty products list for category")
    void shouldReturnEmptyProductsList() throws Exception {
        Long categoryId = 1L;
        CatalogResponseDto response = new CatalogResponseDto();
        CategoryDto category = createCategoryDto(1L, "Шаверма");
        response.setCategory(category);
        response.setProducts(List.of());

        when(catalogService.getProductsByCategory(categoryId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/catalog/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(0));
    }

    private CategoryDto createCategoryDto(Long id, String name) {
        CategoryDto dto = new CategoryDto();
        dto.setId(id);
        dto.setName(name);
        dto.setUrl("https://drive.google.com");
        return dto;
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

    private CatalogResponseDto createCatalogResponse(Long categoryId) {
        CatalogResponseDto response = new CatalogResponseDto();

        CategoryDto category = createCategoryDto(categoryId, "Шаверма");
        response.setCategory(category);

        CatalogProductDto product1 = createCatalogProductDto(12L, "Шаверма Классическая");
        CatalogProductDto product2 = createCatalogProductDto(13L, "Шаверма Сырная");
        response.setProducts(List.of(product1, product2));

        return response;
    }
}
