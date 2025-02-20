package ru.skillbox.currency.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.service.CurrencyService;

/**
 * Контроллер для работы с валютами.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/currency")
public class CurrencyController {
    private final CurrencyService service;

    /**
     * Получает список всех валют.
     *
     * @return Список валют.
     */
    @GetMapping(value = "/")
    ResponseEntity<CurrencyListDto> getAllCurrencies() {
        return ResponseEntity.ok(service.getAllCurrencies());
    }

    /**
     * Получает валюту по её идентификатору.
     *
     * @param id Идентификатор валюты.
     * @return Валюта.
     */
    @GetMapping(value = "/{id}")
    ResponseEntity<CurrencyDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Конвертирует значение валюты.
     *
     * @param value Значение для конвертации.
     * @param numCode Номер кода валюты.
     * @return Конвертированное значение.
     */
    @GetMapping(value = "/convert")
    ResponseEntity<Double> convertValue(@RequestParam("value") Long value, @RequestParam("numCode") Long numCode) {
        return ResponseEntity.ok(service.convertValue(value, numCode));
    }

    /**
     * Создает новую валюту.
     *
     * @param dto Объект валюты для создания.
     * @return Созданная валюта.
     */
    @PostMapping("/create")
    ResponseEntity<CurrencyDto> create(@RequestBody CurrencyDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
}
