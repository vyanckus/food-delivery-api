package com.fedor.fooddelivery;

import com.fedor.fooddelivery.dto.CatalogResponseDto;
import com.fedor.fooddelivery.dto.CategoryDto;
import com.fedor.fooddelivery.entity.Category;
import com.fedor.fooddelivery.entity.Product;
import com.fedor.fooddelivery.repository.CategoryRepository;
import com.fedor.fooddelivery.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Catalog Integration Test")
class CatalogIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("food_delivery_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long pizzaCategoryId;
    private Long sushiCategoryId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category pizzaCategory = new Category();
        pizzaCategory.setName("Пицца");
        pizzaCategory.setUrl("/pizza");
        pizzaCategory = categoryRepository.save(pizzaCategory);
        pizzaCategoryId = pizzaCategory.getId();

        Category sushiCategory = new Category();
        sushiCategory.setName("Суши");
        sushiCategory.setUrl("/sushi");
        sushiCategory = categoryRepository.save(sushiCategory);
        sushiCategoryId = sushiCategory.getId();

        Product pizza1 = new Product();
        pizza1.setName("Маргарита");
        pizza1.setDescription("Классическая пицца");
        pizza1.setPrice(450.0);
        pizza1.setCategory(pizzaCategory);
        pizza1.setUrl("/margarita");
        pizza1.setCurrency("RUB");
        pizza1 = productRepository.save(pizza1);

        Product pizza2 = new Product();
        pizza2.setName("Пепперони");
        pizza2.setDescription("Острая пицца");
        pizza2.setPrice(550.0);
        pizza2.setCategory(pizzaCategory);
        pizza2.setUrl("/pepperoni");
        pizza2.setCurrency("RUB");
        pizza2 = productRepository.save(pizza2);
    }

    @Test
    @DisplayName("Should return all categories")
    void shouldReturnAllCategories() {
        // when
        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange("/catalog", HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(c -> c.getName().equals("Пицца")));
        assertTrue(response.getBody().stream().anyMatch(c -> c.getName().equals("Суши")));
    }

    @Test
    @DisplayName("Should return products by category")
    void shouldReturnProductsByCategory() {
        // when
        ResponseEntity<CatalogResponseDto> response = restTemplate.getForEntity("/catalog/{id}",
                CatalogResponseDto.class, pizzaCategoryId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getCategory());
        assertNotNull(response.getBody().getProducts());
        assertEquals("Пицца", response.getBody().getCategory().getName());
        assertEquals(2, response.getBody().getProducts().size());
        assertTrue(response.getBody().getProducts().stream().anyMatch(p -> p.getName().equals("Маргарита")));
        assertTrue(response.getBody().getProducts().stream().anyMatch(p -> p.getName().equals("Пепперони")));
    }

    @Test
    @DisplayName("Should return empty products for category without products")
    void shouldReturnEmptyProducts_ForCategoryWithoutProducts() {
        // when
        ResponseEntity<CatalogResponseDto> response = restTemplate.getForEntity("/catalog/{id}",
                CatalogResponseDto.class, sushiCategoryId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Суши", response.getBody().getCategory().getName());
        assertTrue(response.getBody().getProducts().isEmpty());
    }

    @Test
    @DisplayName("Should return 404 for non-existent category")
    void shouldReturnNotFound_ForNonExistentCategory() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/catalog/{id}",
                Object.class, 999L);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return correct category structure")
    void shouldReturnCorrectCategoryStructure() {
        // when
        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange("/catalog", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<CategoryDto>>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CategoryDto> categories = response.getBody();
        assertNotNull(categories);

        CategoryDto pizzaCategory = categories.stream()
                .filter(c -> c.getName().equals("Пицца"))
                .findFirst()
                .orElseThrow();

        assertEquals("/pizza", pizzaCategory.getUrl());
        assertNotNull(pizzaCategory.getId());
    }
}
