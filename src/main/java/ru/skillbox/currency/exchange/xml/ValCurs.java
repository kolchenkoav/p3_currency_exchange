package ru.skillbox.currency.exchange.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс, представляющий корневой элемент XML для курса валют.
 */
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ValCurs {
    /**
     * Список валют.
     */
    @XmlElement(name = "Valute")
    private List<СurrencyFromAPI> сurrencyFromAPIS;
}
