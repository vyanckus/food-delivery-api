package com.fedor.fooddelivery;

import com.fedor.fooddelivery.dto.OrderRequestDto;
import com.fedor.fooddelivery.dto.OrderResponseDto;
import com.fedor.fooddelivery.entity.Product;
import com.fedor.fooddelivery.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Order Integration Test")
class OrderIntegrationTest {

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
    private ProductRepository productRepository;

    private Long existingProductId1;
    private Long existingProductId2;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Product product1 = new Product();
        product1.setName("Пицца Маргарита");
        product1.setDescription("Классическая пицца");
        product1.setPrice(450.0);
        product1.setUrl("/margarita");
        product1.setCurrency("RUB");
        product1 = productRepository.save(product1);
        existingProductId1 = product1.getId();

        Product product2 = new Product();
        product2.setName("Пицца Пепперони");
        product2.setDescription("Острая пицца");
        product2.setPrice(550.0);
        product2.setUrl("/pepperoni");
        product2.setCurrency("RUB");
        product2 = productRepository.save(product2);
        existingProductId2 = product2.getId();
    }

    @Test
    @DisplayName("Should create order successfully - end to end")
    void shouldCreateOrder_EndToEnd() {
        // given
        OrderRequestDto request = createValidOrderRequest();

        // when
        ResponseEntity<OrderResponseDto> response = restTemplate.postForEntity("/cart", request, OrderResponseDto.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("You placed the order successfully. Thanks for using our services. Enjoy your food :)",
                response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return 400 when customer name is empty")
    void shouldReturnBadRequest_WhenCustomerNameEmpty() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setCustomerName("");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity("/cart", request, Object.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when phone number is invalid")
    void shouldReturnBadRequest_WhenPhoneNumberInvalid() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setPhoneNumber("invalid-phone");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity("/cart", request, Object.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void shouldReturnNotFound_WhenProductNotFound() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.getItems().get(0).setProductId(999L);

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity("/cart", request, Object.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when order items are empty")
    void shouldReturnBadRequest_WhenOrderItemsEmpty() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setItems(new ArrayList<>());

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity("/cart", request, Object.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 400 when order items are null")
    void shouldReturnBadRequest_WhenOrderItemsNull() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setItems(null);

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity("/cart", request, Object.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private OrderRequestDto createValidOrderRequest() {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerName("Иван Иванов");
        request.setPhoneNumber("+79110001122");

        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();

        OrderRequestDto.OrderItemDto item1 = new OrderRequestDto.OrderItemDto();
        item1.setProductId(existingProductId1);
        item1.setQuantity(2);
        items.add(item1);

        OrderRequestDto.OrderItemDto item2 = new OrderRequestDto.OrderItemDto();
        item2.setProductId(existingProductId2);
        item2.setQuantity(1);
        items.add(item2);

        request.setItems(items);
        return request;
    }
}
