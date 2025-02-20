package ru.skillbox.currency.exchange.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;
import ru.skillbox.currency.exchange.xml.ValCurs;
import ru.skillbox.currency.exchange.xml.СurrencyFromAPI;

import javax.transaction.Transactional;
import java.net.URL;
import java.util.List;

/**
 * Сервис для обновления данных о валютах.
 * Обновление происходит с заданной периодичностью.
 * @see <a href="https://cbr.ru/scripts/XML_daily.asp">Основной API ЦБ</a>
 * @see <a href="https://www.cbr-xml-daily.ru/daily_utf8.xml">Резервный API ЦБ</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyUpdateService {
    private final CurrencyRepository currencyRepository;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.fallback-url}")
    private String fallbackUrl;

    /**
     * Метод для обновления данных о валютах с заданной периодичностью.
     */
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void updateCurrencies() {
        try {
            ValCurs valCurs = fetchCurrencyData();
            updateDatabase(valCurs.getСurrencyFromAPIS());
            log.info("Успешно обновлены данные о валютах из API ЦБ");
        } catch (Exception e) {
            log.error("Не удалось обновить данные о валютах: {}", e.getMessage());
        }
    }

    /**
     * Метод для получения данных о валютах.
     *
     * @return Объект ValCurs с данными о валютах.
     * @throws Exception Если возникла ошибка при получении данных.
     */
    private ValCurs fetchCurrencyData() throws Exception {
        try {
            return fetchFromUrl(apiUrl);
        } catch (Exception e) {
            log.warn("Основной URL не сработал, попытка использовать резервный: {}", e.getMessage());
            return fetchFromUrl(fallbackUrl);
        }
    }

    /**
     * Метод для получения данных о валютах по URL.
     *
     * @param urlString URL для получения данных.
     * @return Объект ValCurs с данными о валютах.
     * @throws Exception Если возникла ошибка при получении данных.
     */
    private ValCurs fetchFromUrl(String urlString) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        URL url = new URL(urlString);
        return (ValCurs) unmarshaller.unmarshal(url);
    }

    /**
     * Метод для обновления данных о валютах в базе данных.
     *
     * @param сurrencyFromAPIS Список объектов Valute с данными о валютах.
     */
    private void updateDatabase(List<СurrencyFromAPI> сurrencyFromAPIS) {
        for (СurrencyFromAPI сurrencyFromAPI : сurrencyFromAPIS) {
            Currency existingCurrency = currencyRepository.findByIsoLetterCode(сurrencyFromAPI.getCharCode());

            Currency currency = existingCurrency != null ? existingCurrency : new Currency();

            currency.setName(сurrencyFromAPI.getName());
            currency.setNominal(сurrencyFromAPI.getNominal());
            currency.setValue(Double.parseDouble(сurrencyFromAPI.getValue().replace(",", ".")));
            currency.setIsoNumCode(сurrencyFromAPI.getNumCode());
            currency.setIsoLetterCode(сurrencyFromAPI.getCharCode());

            currencyRepository.save(currency);
        }
    }
}