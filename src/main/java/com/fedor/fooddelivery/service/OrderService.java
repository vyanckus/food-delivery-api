package com.fedor.fooddelivery.service;

import com.fedor.fooddelivery.dto.OrderRequestDto;
import com.fedor.fooddelivery.dto.OrderResponseDto;
import com.fedor.fooddelivery.exceptions.InvalidOrderException;
import com.fedor.fooddelivery.exceptions.ProductNotFoundException;
import com.fedor.fooddelivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для обработки заказов.
 * Выполняет валидацию данных и создание заказов
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;

    /**
     * Указано регулярное выражение для проверки телефонного номера РФ.
     * Форматы: +7 XXX XXX XX XX, 8 XXX XXX XX XX, с различными разделителями
     * Для проверки телефонного номера независимо от страны необходимо заменить на
     * private static final String PHONE_PATTERN = "^[+]?[\\d\\s\\-\\.\\(\\)]{5,20}$";
     */
    private static final String PHONE_PATTERN = "^(\\+7|8)[\\s-]?(\\(\\d{3}\\)|\\d{3})[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    /**
     * Создать новый заказ
     * @param orderRequest DTO с данными заказа
     * @return DTO ответа с результатом создания заказа
     * @throws InvalidOrderException если данные заказа невалидны
     * @throws ProductNotFoundException если товар не найден
     */
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        log.info("Начало создания заказа для клиента: {}",
                orderRequest.getCustomerName() != null ? orderRequest.getCustomerName() : "не указано");

        validateCustomerName(orderRequest);
        validatePhoneNumber(orderRequest);
        validateExistingProducts(orderRequest);

        log.info("Заказ успешно создан для клиента: {}", orderRequest.getCustomerName());
        return new OrderResponseDto(true);
    }

    /**
     * Валидация имени клиента
     * @param orderRequest DTO заказа
     * @throws InvalidOrderException если имя не указано
     */
    private void validateCustomerName(OrderRequestDto orderRequest) {
        log.debug("Валидация имени клиента");

        if (isBlank(orderRequest.getCustomerName())) {
            log.error("Ошибка валидации: не указано имя клиента");
            throw new InvalidOrderException("Не указано имя клиента");
        }

        log.debug("Имя клиента прошло валидацию: {}", orderRequest.getCustomerName());
    }

    /**
     * Проверка строки на пустоту
     * @param data строка для проверки
     * @return true если строка null или пустая/пробелы
     */
    private boolean isBlank(String data) {
        return data == null || data.trim().isEmpty();
    }

    /**
     * Валидация номера телефона
     * @param orderRequest DTO заказа
     * @throws InvalidOrderException если номер не указан или невалиден
     */
    private void validatePhoneNumber(OrderRequestDto orderRequest) {
        log.debug("Валидация номера телефона");

        if (isBlank(orderRequest.getPhoneNumber())) {
            log.error("Ошибка валидации: не указан номер телефона");
            throw new InvalidOrderException("Не указан номер телефона");
        }

        String phoneNumber = orderRequest.getPhoneNumber().trim();
        Matcher matcher = pattern.matcher(phoneNumber);

        if (!matcher.matches()) {
            log.error("Ошибка валидации: неверный формат номера телефона: {}", phoneNumber);
            throw new InvalidOrderException("Указан неверный номер телефона");
        }

        log.debug("Номер телефона прошел валидацию: {}", phoneNumber);
    }

    /**
     * Валидация существования товаров в заказе
     * @param orderRequest DTO заказа
     * @throws InvalidOrderException если заказ пустой
     * @throws ProductNotFoundException если товар не найден
     */
    private void validateExistingProducts(OrderRequestDto orderRequest) {
        log.debug("Валидация товаров в заказе");

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            log.error("Ошибка валидации: заказ не содержит товаров");
            throw new InvalidOrderException("Заказ не может быть пустым");
        }

        for (OrderRequestDto.OrderItemDto item : orderRequest.getItems()) {
            if (!productRepository.existsById(item.getProductId())) {
                log.error("Товар с ID {} не найден в базе данных", item.getProductId());
                throw new ProductNotFoundException(item.getProductId());
            }
        }

        log.debug("Все {} товаров в заказе существуют в базе данных", orderRequest.getItems().size());
    }
}
