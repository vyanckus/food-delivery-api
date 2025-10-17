package com.fedor.fooddelivery;

import com.fedor.fooddelivery.dto.OrderRequestDto;
import com.fedor.fooddelivery.dto.OrderResponseDto;
import com.fedor.fooddelivery.exceptions.InvalidOrderException;
import com.fedor.fooddelivery.exceptions.ProductNotFoundException;
import com.fedor.fooddelivery.repository.ProductRepository;
import com.fedor.fooddelivery.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Test")
class OrderServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order when request is valid")
    void shouldCreateOrder_WhenValidRequest() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        when(productRepository.existsById(11L)).thenReturn(true);
        when(productRepository.existsById(12L)).thenReturn(true);

        // when
        OrderResponseDto response = orderService.createOrder(request);

        // then
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessage());
        assertEquals("You placed the order successfully. Thanks for using our services. Enjoy your food :)",
                response.getMessage());
        verify(productRepository, times(1)).existsById(11L);
        verify(productRepository, times(1)).existsById(12L);
        verify(productRepository, never()).findById(anyLong());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"     ", "\t", "\n", "   \t   "})
    @DisplayName("Should throw exception when customer name is blank")
    void shouldThrowException_WhenCustomerNameIsBlank(String blankName) {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setCustomerName(blankName);

        // when & then
        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(request));
        assertEquals("Не указано имя клиента", exception.getMessage());
        verify(productRepository, never()).existsById(anyLong());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"           ", "\t", "\n"})
    @DisplayName("Should throw exception when phone number is blank")
    void shouldThrowException_WhenPhoneNumberIsBlank(String blankPhoneNumber) {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setPhoneNumber(blankPhoneNumber);

        // when & then
        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(request));
        assertEquals("Не указан номер телефона", exception.getMessage());
        verify(productRepository, never()).existsById(anyLong());
    }

    /**
     * Данный тест проверяет выбрасывание исключения, если указаны невалидные телефонные номера РФ.
     * Если приложение будет рассчитано на телефонные номера независимо от страны, то данный тест можно просто закомментировать
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "9110001122",
            "891100011223",
            "99110001122"
    })
    @DisplayName("Should throw exception when phone number is invalid")
    void shouldThrowException_WhenInvalidPhoneNumber(String invalidPhoneNumber) {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setPhoneNumber(invalidPhoneNumber);

        // when & then
        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(request));
        assertEquals("Указан неверный номер телефона", exception.getMessage());
        verify(productRepository, never()).existsById(anyLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+79110001122",
            "89110001122",
            "+7 911 000 11 22",
            "8 (911) 000-11-22",
            "+7(911)000-11-22",
            "8-911-000-11-22",
            "+7 911 000 11 22"
    })
    @DisplayName("Should accept valid phone number formats")
    void shouldAcceptValidPhoneNumberFormats(String validPhoneNumber) {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setPhoneNumber(validPhoneNumber);
        when(productRepository.existsById(11L)).thenReturn(true);
        when(productRepository.existsById(12L)).thenReturn(true);

        // when
        OrderResponseDto response = orderService.createOrder(request);

        // then
        assertTrue(response.isSuccess());
        verify(productRepository, times(1)).existsById(11L);
        verify(productRepository, times(1)).existsById(12L);
    }

    @ParameterizedTest
    @MethodSource("invalidOrderItemsProvider")
    @DisplayName("Should throw exception when order items are invalid")
    void shouldThrowException_WhenOrderItemsAreInvalid(List<OrderRequestDto.OrderItemDto> invalidItems) {
        // given
        OrderRequestDto request = createValidOrderRequest();
        request.setItems(invalidItems);

        // when & then
        InvalidOrderException exception = assertThrows(InvalidOrderException.class,
                () -> orderService.createOrder(request));
        assertEquals("Заказ не может быть пустым", exception.getMessage());
        verify(productRepository, never()).existsById(anyLong());
    }

    private static Stream<Arguments> invalidOrderItemsProvider() {
        return Stream.of(Arguments.of((List<OrderRequestDto.OrderItemDto>) null),
                Arguments.of(List.of())
        );
    }

    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowException_WhenProductNotFound() {
        // given
        OrderRequestDto request = createValidOrderRequest();
        when(productRepository.existsById(11L)).thenReturn(true);
        when(productRepository.existsById(12L)).thenReturn(false);

        // when & then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> orderService.createOrder(request));
        assertEquals("Блюдо с ID 12 не найдено", exception.getMessage());
        verify(productRepository, times(1)).existsById(11L);
        verify(productRepository, times(1)).existsById(12L);
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
