package com.fedor.fooddelivery.controller;

import com.fedor.fooddelivery.dto.OrderRequestDto;
import com.fedor.fooddelivery.dto.OrderResponseDto;
import com.fedor.fooddelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки заказов.
 * Обрабатывает операции связанные с созданием и управлением заказами
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    /**
     * Создать новый заказ
     * POST /cart
     *
     * @param orderRequest DTO с данными заказа (имя, телефон, товары)
     * @return DTO ответа с результатом создания заказа
     */
    @PostMapping("/cart")
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequest) {
        log.info("HTTP POST /cart - запрос на создание заказа. Клиент: {}, товаров: {}",
                orderRequest.getCustomerName() != null ? orderRequest.getCustomerName() : "не указано",
                orderRequest.getItems() != null ? orderRequest.getItems().size() : 0);

        OrderResponseDto response = orderService.createOrder(orderRequest);

        log.info("HTTP POST /cart - заказ успешно создан для клиента: {}",
                orderRequest.getCustomerName() != null ? orderRequest.getCustomerName() : "не указано");
        return response;
    }
}
