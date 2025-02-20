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

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyMapper mapper;
    private final CurrencyRepository repository;

    public CurrencyDto getById(Long id) {
        log.info("CurrencyService method getById executed");
        Currency currency = repository.findById(id).orElseThrow(() -> new RuntimeException("Currency not found with id: " + id));
        return mapper.convertToDto(currency);
    }

    public Double convertValue(Long value, Long numCode) {
        log.info("CurrencyService method convertValue executed");
        Currency currency = repository.findByIsoNumCode(numCode);
        return value * currency.getValue();
    }

    public CurrencyDto create(CurrencyDto dto) {
        log.info("CurrencyService method create executed");
        return  mapper.convertToDto(repository.save(mapper.convertToEntity(dto)));
    }

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
