# Сервис Обмен валюты
Приложение для практического кейса "Обмен Валюты" обучающей платформы [Skillbox](https://skillbox.ru)

## 📌 Описание проекта
Currency Exchange Service — это микросервис на **Spring Boot**, который позволяет получать информацию о курсах валют, конвертировать валюты и обновлять их из API Центрального банка РФ.

## 🚀 Функциональность
- Получение списка всех валют.
- Получение информации о конкретной валюте по ID.
- Конвертация суммы из одной валюты в другую.
- Автоматическое обновление курсов валют с ЦБ РФ.

## 🛠️ Стек технологий
- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Scheduling
- **База данных:** PostgreSQL, Liquibase
- **Интеграция:** MapStruct, Testcontainers, WireMock
- **Логирование:** Slf4j, Lombok
- **Тестирование:** JUnit 5, Mockito, Testcontainers

## 📦 Установка и запуск
### 🔹 Требования
- **JDK 17+**
- **Docker & Docker Compose** (для тестирования с Testcontainers)
- **PostgreSQL** (если не использовать Docker)

### 🔹 Клонирование репозитория
```sh
git clone https://github.com/your-repository/currency-exchange.git
cd currency-exchange
```

### 🔹 Настройка базы данных
Создайте базу данных PostgreSQL:
```sh
CREATE DATABASE currency_exchange;
```
Настройте `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/currency_exchange
    username: postgres
    password: postgres
```

### 🔹 Запуск приложения
```sh
./mvnw spring-boot:run
```


## 🛠️ Основные изменения

- Добавление нового метода в контроллер для вывода всех валют.
- Получение доступных валют из открытого источника.
- Автоматическое обновление курсов валют с ЦБ РФ.
- Добавление тестов для проверки работы контроллера и сервиса.

## 🎯 API Эндпоинты
| Метод | URL | Описание |
|--------|-----------------|----------------|
| **GET** | `/api/currency/` | Получить список всех валют |
| **GET** | `/api/currency/{id}` | Получить информацию о валюте по ID |
| **GET** | `/api/currency/convert?value=100&numCode=840` | Конвертировать 100 единиц валюты с кодом 840 |
| **POST** | `/api/currency/create` | Добавить новую валюту |

## 🧪 Тестирование
Проект содержит модульные и интеграционные тесты с **JUnit 5, Mockito, Testcontainers**.
Запустить тесты можно командой:
```sh
./mvnw test
```
