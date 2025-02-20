package ru.skillbox.currency.exchange.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.service.CurrencyService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService service;

    @DisplayName("Test getting all currencies successfully")
    @Test
    void getAllCurrencies_Success() throws Exception {
        when(service.getAllCurrencies()).thenReturn(new CurrencyListDto());

        mockMvc.perform(get("/api/currency/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).getAllCurrencies();
    }

    @DisplayName("Test getting currency by ID successfully")
    @Test
    void getById_Success() throws Exception {
        CurrencyDto dto = new CurrencyDto(1L, "USD", 1L, 93.5224, 840L, "USD");
        when(service.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/currency/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("USD"));

        verify(service).getById(1L);
    }

    @DisplayName("Test converting currency value successfully")
    @Test
    void convertValue_Success() throws Exception {
        when(service.convertValue(2L, 840L)).thenReturn(187.0448);

        mockMvc.perform(get("/api/currency/convert")
                        .param("value", "2")
                        .param("numCode", "840"))
                .andExpect(status().isOk())
                .andExpect(content().string("187.0448"));

        verify(service).convertValue(2L, 840L);
    }

    @DisplayName("Test creating a new currency successfully")
    @Test
    void create_Success() throws Exception {
        CurrencyDto dto = new CurrencyDto(1L, "USD", 1L, 93.5224, 840L, "USD");
        when(service.create(any(CurrencyDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/currency/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"USD\",\"nominal\":1,\"value\":93.5224,\"isoNumCode\":840,\"isoLetterCode\":\"USD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USD"));

        verify(service).create(any(CurrencyDto.class));
    }
}