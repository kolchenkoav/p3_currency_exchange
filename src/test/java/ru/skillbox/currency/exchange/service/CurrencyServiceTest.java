package ru.skillbox.currency.exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository repository;

    @Mock
    private CurrencyMapper mapper;

    @InjectMocks
    private CurrencyService service;

    private Currency currency;
    private CurrencyDto currencyDto;

    @BeforeEach
    void setUp() {
        currency = new Currency(1L, "USD", 1L, 93.5224, 840L, "USD");
        currencyDto = new CurrencyDto(1L, "USD", 1L, 93.5224, 840L, "USD");
    }

    @Test
    @DisplayName("Get Currency by ID - Success")
    void getById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(currency));
        when(mapper.convertToDto(currency)).thenReturn(currencyDto);

        CurrencyDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals(currencyDto.getId(), result.getId());
        verify(repository).findById(1L);
        verify(mapper).convertToDto(currency);
    }

    @Test
    @DisplayName("Get Currency by ID - Not Found")
    void getById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.getById(1L));
        assertEquals("Currency not found with id: 1", exception.getMessage());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Convert Currency Value - Success")
    void convertValue_Success() {
        when(repository.findByIsoNumCode(840L)).thenReturn(currency);

        Double result = service.convertValue(2L, 840L);

        assertEquals(187.0448, result, 0.0001);
        verify(repository).findByIsoNumCode(840L);
    }

    @Test
    @DisplayName("Create Currency - Success")
    void create_Success() {
        when(mapper.convertToEntity(currencyDto)).thenReturn(currency);
        when(repository.save(currency)).thenReturn(currency);
        when(mapper.convertToDto(currency)).thenReturn(currencyDto);

        CurrencyDto result = service.create(currencyDto);

        assertNotNull(result);
        assertEquals(currencyDto.getId(), result.getId());
        verify(mapper).convertToEntity(currencyDto);
        verify(repository).save(currency);
        verify(mapper).convertToDto(currency);
    }

    @Test
    @DisplayName("Get All Currencies - Success")
    void getAllCurrencies_Success() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(currency);
        when(repository.findAll()).thenReturn(currencies);

        CurrencyListDto result = service.getAllCurrencies();

        assertNotNull(result);
        assertEquals(1, result.getCurrencies().size());
        assertEquals(currency.getName(), result.getCurrencies().get(0).getName());
        verify(repository).findAll();
    }
}