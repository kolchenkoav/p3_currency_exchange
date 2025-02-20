package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с валютами.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyMapper mapper;
    private final CurrencyRepository repository;

    /**
     * Получает валюту по идентификатору.
     *
     * @param id идентификатор валюты
     * @return DTO валюты
     */
    public CurrencyDto getById(Long id) {
        log.info("CurrencyService method getById executed");
        Currency currency = repository.findById(id).orElseThrow(() -> new RuntimeException("Currency not found with id: " + id));
        return mapper.convertToDto(currency);
    }

    /**
     * Конвертирует значение валюты.
     *
     * @param value значение для конвертации
     * @param numCode числовой код валюты
     * @return конвертированное значение
     */
    public Double convertValue(Long value, Long numCode) {
        log.info("CurrencyService method convertValue executed");
        Currency currency = repository.findByIsoNumCode(numCode);
        return value * currency.getValue();
    }

    /**
     * Создает новую валюту.
     *
     * @param dto DTO валюты
     * @return созданная валюта
     */
    public CurrencyDto create(CurrencyDto dto) {
        log.info("CurrencyService method create executed");
        return  mapper.convertToDto(repository.save(mapper.convertToEntity(dto)));
    }

    /**
     * Получает все валюты.
     *
     * @return список всех валют
     */
    public CurrencyListDto getAllCurrencies() {
        log.info("CurrencyService method getAllCurrencies executed");
        List<Currency> currencies = repository.findAll();
        List<CurrencyListDto.CurrencyItemDto> currencyItems = currencies.stream()
                .map(currency -> {
                    CurrencyListDto.CurrencyItemDto item = new CurrencyListDto.CurrencyItemDto();
                    item.setName(currency.getName());
                    item.setValue(currency.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        CurrencyListDto response = new CurrencyListDto();
        response.setCurrencies(currencyItems);
        return response;
    }
}
