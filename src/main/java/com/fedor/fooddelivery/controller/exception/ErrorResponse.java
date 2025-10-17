package com.fedor.fooddelivery.controller.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO для структурированного ответа с ошибкой.
 * Содержит сообщение, временную метку и HTTP статус
 */
@Getter
public class ErrorResponse {
    private final String message;
    private final LocalDateTime timestamp;
    private final int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now(); // Автоматически устанавливаем текущее время
    }
}
