// File: src/main/java/ru/skillbox/currency/exchange/service/CurrencyUpdateService.java
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
import ru.skillbox.currency.exchange.xml.Valute;

import javax.transaction.Transactional;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyUpdateService {
    private final CurrencyRepository currencyRepository;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.fallback-url}")
    private String fallbackUrl;

    @Scheduled(fixedRate = 360000)
    @Transactional
    public void updateCurrencies() {
        try {
            ValCurs valCurs = fetchCurrencyData();
            updateDatabase(valCurs.getValutes());
            log.info("Successfully updated currencies from CBR API");
        } catch (Exception e) {
            log.error("Failed to update currencies: {}", e.getMessage());
        }
    }

    private ValCurs fetchCurrencyData() throws Exception {
        try {
            return fetchFromUrl(apiUrl);
        } catch (Exception e) {
            log.warn("Primary URL failed, trying fallback: {}", e.getMessage());
            return fetchFromUrl(fallbackUrl);
        }
    }

    private ValCurs fetchFromUrl(String urlString) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        URL url = new URL(urlString);
        return (ValCurs) unmarshaller.unmarshal(url);
    }

    private void updateDatabase(List<Valute> valutes) {
        for (Valute valute : valutes) {
            Currency existingCurrency = currencyRepository.findByIsoLetterCode(valute.getCharCode());

            Currency currency = existingCurrency != null ? existingCurrency : new Currency();

            currency.setName(valute.getName());
            currency.setNominal(valute.getNominal());
            currency.setValue(Double.parseDouble(valute.getValue().replace(",", ".")));
            currency.setIsoNumCode(valute.getNumCode());
            currency.setIsoLetterCode(valute.getCharCode());

            currencyRepository.save(currency);
        }
    }
}