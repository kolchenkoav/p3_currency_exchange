package ru.skillbox.currency.exchange.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrencyListDto {
    private List<CurrencyItemDto> currencies;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CurrencyItemDto {
        private String name;
        private Double value;
    }
}