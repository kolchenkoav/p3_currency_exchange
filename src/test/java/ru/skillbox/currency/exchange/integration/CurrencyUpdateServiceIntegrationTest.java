package ru.skillbox.currency.exchange.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;
import ru.skillbox.currency.exchange.service.CurrencyUpdateService;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class CurrencyUpdateServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    private static WireMockServer wireMockServer;

    @Autowired
    private CurrencyUpdateService currencyUpdateService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.xml");
        registry.add("currency.api.url", () -> "http://localhost:" + wireMockServer.port() + "/api");
        registry.add("currency.api.fallback-url", () -> "http://localhost:" + wireMockServer.port() + "/fallback");
    }

    @BeforeAll
    static void setUp() {
        postgres.start();
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());
        stubFor(get(urlEqualTo("/api")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody("<ValCurs>" +
                        "<Valute ID=\"R01235\">" +
                        "<NumCode>840</NumCode>" +
                        "<CharCode>USD</CharCode>" +
                        "<Nominal>1</Nominal>" +
                        "<Name>Доллар США</Name>" +
                        "<Value>93,5224</Value>" +
                        "</Valute>" +
                        "</ValCurs>")));
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
        postgres.stop();
    }

    @BeforeEach
    void clearDatabase() {
        currencyRepository.deleteAll(); // Очищаем базу перед каждым тестом
    }

    @DisplayName("Test Update Currencies From API")
    @Test
    void testUpdateCurrenciesFromApi() {
        // Выполняем обновление валют
        currencyUpdateService.updateCurrencies();

        // Проверяем, что валюта добавлена в базу
        Currency usd = currencyRepository.findByIsoLetterCode("USD");
        assertNotNull(usd);
        assertEquals("Доллар США", usd.getName());
        assertEquals(93.5224, usd.getValue(), 0.0001);
        assertEquals(840L, usd.getIsoNumCode());
    }
}