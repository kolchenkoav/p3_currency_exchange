package ru.skillbox.currency.exchange.mapper;

import org.mapstruct.Mapper;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;

/**
 * Маппер для преобразования между сущностями Currency и DTO CurrencyDto.
 */
@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    /**
     * Преобразует сущность Currency в DTO CurrencyDto.
     *
     * @param currency сущность Currency
     * @return DTO CurrencyDto
     */
    CurrencyDto convertToDto(Currency currency);

    /**
     * Преобразует DTO CurrencyDto в сущность Currency.
     *
     * @param currencyDto DTO CurrencyDto
     * @return сущность Currency
     */
    Currency convertToEntity(CurrencyDto currencyDto);
}
