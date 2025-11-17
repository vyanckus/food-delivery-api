# Food Delivery API

REST API для сервиса доставки еды. Приложение предоставляет каталог блюд и функционал оформления заказов.

## 🔄 CI/CD Pipeline

![CI/CD Status](https://github.com/vyanckus/food-delivery-api/actions/workflows/ci-cd.yml/badge.svg)

Проект использует автоматизированный CI/CD pipeline на GitHub Actions:

### 🤖 Автоматические проверки при каждом коммите:

*   ✅ Тестирование \- запуск всех unit и интеграционных тестов

*   ✅ Сборка \- компиляция кода и создание JAR-файла

*   ✅ Docker \- сборка Docker образа

*   ✅ Качество кода \- проверка покрытия тестами (JaCoCo)


### 📊 Pipeline Stages:

text

```
Push/Pull Request → Тестирование → Сборка JAR → Docker Build → Quality Gate
```

### 🎯 Результаты pipeline:

*   Отчеты тестирования \- доступны в разделе Actions → Artifacts

*   Покрытие кода \- JaCoCo отчет в формате HTML

*   Готовность к деплою \- после успешного прохождения всех этапов


### 🚀 Автоматический деплой:

При пуше в ветку `main` происходит автоматический деплой на [Railway.com](https://railway.com/)

## 🌐 Работающее приложение

Production версия развернута и доступна по адресу:


```
https://food-delivery-api-production-bee8.up.railway.app
```

Примеры работающих запросов:


```
# Получить все категории
https://food-delivery-api-production-bee8.up.railway.app/catalog

# Получить товары категории 1
https://food-delivery-api-production-bee8.up.railway.app/catalog/1
```

## 🚀 Функциональность

- **Каталог товаров** - получение категорий и товаров
- **Управление заказами** - создание новых заказов с валидацией
- **Валидация данных** - проверка телефона, имени, существования товаров

## 🛠 Технологии

- **Java 17**
- **Spring Boot 3**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Maven**
- **JUnit 5 & Mockito**
- **Testcontainers**


## 📋 API Endpoints

### Каталог
- `GET /catalog` - получить все категории
- `GET /catalog/{id}` - получить товары по категории

### Заказы
- `POST /cart` - создать новый заказ

## 📝 Примеры запросов

### Получить категории

http

```
GET http://localhost:8080/catalog
```

### Получить товары категории

http

```
GET http://localhost:8080/catalog/1
```
### Создать заказ

http

```
POST http://localhost:8080/cart
Content-Type: application/json

{
  "customerName": "Иван Иванов",
  "phoneNumber": "+79161234567",
  "items": [
    {
      "productId": 11,
      "quantity": 2
    },
    {
      "productId": 12,
      "quantity": 1
    }
  ]
}
```

## 🗃️ Настройка базы данных

### Локальная разработка

properties

```
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/food_delivery
spring.datasource.username=postgres
spring.datasource.password=ваш_пароль

# application-local.properties (в .gitignore)
spring.datasource.password=ваш_локальный_пароль
```

### Production ([Railway](https://railway.com/))

*   Автоматически развертывается через Docker
*   Переменные окружения настраиваются в Railway Dashboard
*   База данных PostgreSQL предоставляется как отдельный сервис Railway
*   Приложение доступно по адресу: `https://food-delivery-api-production-bee8.up.railway.app`


## 📊 Логирование

Приложение использует многоуровневое логирование через SLF4J:

### Уровни логирования:

*   ERROR \- критические ошибки и исключения

*   WARN \- предупреждения и нестандартные ситуации

*   INFO \- основная информация о работе приложения

*   DEBUG \- детальная отладочная информация

*   TRACE \- максимально подробное логирование

## 📚 Генерация JavaDoc

### Сгенерировать документацию:

bash

```
mvn javadoc:javadoc
```

### Просмотреть документацию:

Открой файл: `target/reports/apidocs/index.html`

Все классы и методы снабжены комментариями на русском языке.

## 📁 Структура проекта

text

```
src/
├── main/java/com/fedor/fooddelivery/
│   ├── controller/     # REST контроллеры
│   ├── service/        # Бизнес-логика
│   ├── repository/     # Доступ к данным
│   ├── entity/         # JPA сущности
│   ├── dto/            # Data Transfer Objects
│   ├── exceptions/     # Кастомные исключения
│   └── mapper/         # Мапперы для DTO
└── test/java/          # Unit-тесты и интеграционные тесты
```


## 🧪 Тестирование

Проект включает комплексную систему тестирования с полным покрытием всех компонентов:

### Unit-тесты

*   `CatalogControllerTest` \- тестирование эндпоинтов каталога

*   `CatalogServiceTest` \- бизнес-логика работы с каталогом

*   `OrderControllerTest` \- тестирование эндпоинтов заказов

*   `OrderServiceTest` \- бизнес-логика создания заказов и валидации

*   `GlobalExceptionHandlerTest` \- обработка исключений и HTTP статусов


### Интеграционные тесты

*   `OrderIntegrationTest` \- сквозное тестирование создания заказов (HTTP → Service → Database)

*   `CatalogIntegrationTest` \- полная проверка работы каталога

*   `FoodDeliveryApplicationTests` \- проверка загрузки Spring контекста


### Особенности тестирования

*   ✅ Testcontainers \- изолированная тестовая среда с PostgreSQL

*   ✅ Полное покрытие \- от HTTP запросов до работы с БД

*   ✅ Валидация сценариев \- успешные кейсы и обработка ошибок

*   ✅ Изоляция тестов \- каждый тест работает с чистой БД


### Запуск тестов с отчетом покрытия
```bash
mvn clean test jacoco:report
```
Отчет будет доступен по пути: `target/site/jacoco/index.html`

### Стратегия тестирования

*   Unit-тесты \- быстрая проверка изолированной логики

*   Интеграционные тесты \- проверка взаимодействия компонентов

*   MockMvc \- тестирование контроллеров без запуска сервера

*   TestRestTemplate \- полные HTTP запросы в интеграционных тестах


Тесты гарантируют корректную работу API и обработку граничных случаев, включая валидацию телефонных номеров, проверку существования товаров и обработку ошибок.

## 📦 Быстрый запуск (Docker - рекомендуется)

### Предварительные требования:
- Установленный Docker и Docker Compose

### Запуск в Docker:
```bash
mvn clean package
docker-compose up --build
```
Приложение будет доступно по адресу: `http://localhost:8080`

## 👨‍💻 Разработчик

Вянцкус Фёдор