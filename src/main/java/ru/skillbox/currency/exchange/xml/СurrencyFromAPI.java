package ru.skillbox.currency.exchange.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "Valute")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class СurrencyFromAPI {
    @XmlAttribute(name = "ID")
    private String id;

    @XmlElement(name = "NumCode")
    private Long numCode;

    @XmlElement(name = "CharCode")
    private String charCode;

    @XmlElement(name = "Nominal")
    private Long nominal;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Value")
    private String value;
}