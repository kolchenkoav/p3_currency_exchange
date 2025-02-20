package ru.skillbox.currency.exchange.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CurrencyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CurrencyRepository currencyRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate"); // Используем validate, так как Liquibase управляет схемой
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.xml");
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void clearDatabase() {
        currencyRepository.deleteAll();
    }

    @Test
    @DisplayName("Test Create and Get Currency")
    void testCreateAndGetCurrency() {
        CurrencyDto requestDto = new CurrencyDto(null, "USD", 1L, 93.5224, 840L, "USD");
        HttpEntity<CurrencyDto> request = new HttpEntity<>(requestDto, new HttpHeaders());

        ResponseEntity<CurrencyDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/currency/create",
                HttpMethod.POST,
                request,
                CurrencyDto.class
        );

        assertEquals(200, response.getStatusCodeValue());
        CurrencyDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertNotNull(responseDto.getId());
        assertEquals("USD", responseDto.getName());

        // Проверка в базе данных
        Currency currency = currencyRepository.findById(responseDto.getId()).orElse(null);
        assertNotNull(currency);
        assertEquals("USD", currency.getName());
        assertEquals(93.5224, currency.getValue(), 0.0001);

        // Получение валюты по ID через API
        ResponseEntity<CurrencyDto> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/currency/" + responseDto.getId(),
                CurrencyDto.class
        );

        assertEquals(200, getResponse.getStatusCodeValue());
        assertEquals("USD", getResponse.getBody().getName());
    }

    @Test
    @DisplayName("Test Get All Currencies")
    void testGetAllCurrencies() {
        Currency currency = new Currency(null, "EUR", 1L, 99.5534, 978L, "EUR");
        currencyRepository.save(currency);

        // Вызов API
        ResponseEntity<CurrencyListDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/currency/",
                CurrencyListDto.class
        );

        assertEquals(200, response.getStatusCodeValue());
        CurrencyListDto listDto = response.getBody();
        assertNotNull(listDto);
        assertFalse(listDto.getCurrencies().isEmpty());

        // Проверяем, что "EUR" присутствует в списке
        List<CurrencyListDto.CurrencyItemDto> currencies = listDto.getCurrencies();
        assertTrue(currencies.stream().anyMatch(item -> "EUR".equals(item.getName())));
        CurrencyListDto.CurrencyItemDto eurItem = currencies.stream()
                .filter(item -> "EUR".equals(item.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(eurItem);
        assertEquals(99.5534, eurItem.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Test Convert Value")
    void testConvertValue() {
        Currency currency = new Currency(null, "USD", 1L, 93.5224, 840L, "USD");
        currencyRepository.save(currency);

        // Вызов API для конвертации
        ResponseEntity<Double> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/currency/convert?value=2&numCode=840",
                Double.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(187.0448, response.getBody(), 0.0001);
    }
}