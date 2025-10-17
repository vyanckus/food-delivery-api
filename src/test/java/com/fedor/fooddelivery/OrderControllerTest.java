package com.fedor.fooddelivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedor.fooddelivery.controller.OrderController;
import com.fedor.fooddelivery.dto.OrderRequestDto;
import com.fedor.fooddelivery.dto.OrderResponseDto;
import com.fedor.fooddelivery.exceptions.InvalidOrderException;
import com.fedor.fooddelivery.exceptions.ProductNotFoundException;
import com.fedor.fooddelivery.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("Order Controller Test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrder() throws Exception {
        // given
        OrderRequestDto request = createValidOrderRequest();
        OrderResponseDto response = new OrderResponseDto(true);

        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You placed the order successfully. Thanks for using our services. Enjoy your food :)"));
    }

    @Test
    @DisplayName("Should return 400 when order validation fails")
    void shouldReturnBadRequest_WhenOrderInvalid() throws Exception {
        // given
        OrderRequestDto request = createValidOrderRequest();

        when(orderService.createOrder(any(OrderRequestDto.class)))
                .thenThrow(new InvalidOrderException("Не указано имя клиента"));

        // when & then
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Не указано имя клиента"));
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void shouldReturnNotFound_WhenProductNotFound() throws Exception {
        // given
        OrderRequestDto request = createValidOrderRequest();

        when(orderService.createOrder(any(OrderRequestDto.class)))
                .thenThrow(new ProductNotFoundException(999L));

        // when & then
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Блюдо с ID 999 не найдено"));
    }

    private OrderRequestDto createValidOrderRequest() {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerName("Иван Иванов");
        request.setPhoneNumber("+79110001122");

        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();

        OrderRequestDto.OrderItemDto item1 = new OrderRequestDto.OrderItemDto();
        item1.setProductId(11L);
        item1.setQuantity(2);
        items.add(item1);

        OrderRequestDto.OrderItemDto item2 = new OrderRequestDto.OrderItemDto();
        item2.setProductId(12L);
        item2.setQuantity(1);
        items.add(item2);

        request.setItems(items);
        return request;
    }
}
