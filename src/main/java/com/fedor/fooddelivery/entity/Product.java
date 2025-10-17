package com.fedor.fooddelivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс-сущность, представляющий продукт питания в системе доставки.
 * Каждый продукт принадлежит определенной категории.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    // Связь многие-к-одному с категорией
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "currency", nullable = false)
    private String currency = "RUB"; // Валюта по умолчанию - российский рубль
}
