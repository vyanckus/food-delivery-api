package com.fedor.fooddelivery.controller.exception;

import com.fedor.fooddelivery.exceptions.CategoryNotFoundException;
import com.fedor.fooddelivery.exceptions.InvalidOrderException;
import com.fedor.fooddelivery.exceptions.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST контроллеров
 * Перехватывает исключения и возвращает структурированные JSON ответы
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обработка исключений "Не найдено" (404)
     * Обрабатывает CategoryNotFoundException и ProductNotFoundException
     */
    @ExceptionHandler({CategoryNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        log.warn("Ошибка 404 Not Found: {}", ex.getMessage());
        return createResponse(ex, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка исключений "Неверный запрос" (400)
     * Обрабатывает ошибки валидации заказа
     */
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidOrderException ex) {
        log.warn("Ошибка 400 Bad Request: {}", ex.getMessage());
        return createResponse(ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка всех остальных исключений (500)
     * Перехватывает любые непредвиденные ошибки
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Ошибка 500 Internal Server Error: {}", ex.getMessage(), ex);
        return createResponse(new Exception("Внутренняя ошибка сервера"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Создание структурированного ответа с ошибкой
     *
     * @param exception исключение
     * @param status HTTP статус
     * @return ResponseEntity с ErrorResponse
     */
    private ResponseEntity<ErrorResponse> createResponse(Exception exception, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), status.value());
        log.debug("Создан ErrorResponse: message={}, status={}", error.getMessage(), error.getStatus());
        return ResponseEntity.status(status).body(error);
    }
}
