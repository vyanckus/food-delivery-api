package com.fedor.fooddelivery;

import com.fedor.fooddelivery.exceptions.CategoryNotFoundException;
import com.fedor.fooddelivery.exceptions.InvalidOrderException;
import com.fedor.fooddelivery.exceptions.ProductNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-exceptions")
public class TestExceptionController {
    @GetMapping("/category-not-found")
    String testCategoryNotFound() {
        throw new CategoryNotFoundException(999L);
    }

    @GetMapping("/product-not-found")
    String testProductNotFound() {
        throw new ProductNotFoundException(888L);
    }

    @GetMapping("/invalid-order")
    String testInvalidOrder() {
        throw new InvalidOrderException("Невалидный заказ");
    }

    @GetMapping("/generic-exception")
    String testGenericException() {
        throw new RuntimeException("Внутренняя ошибка сервера");
    }
}
