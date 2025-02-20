package ru.skillbox.currency.exchange.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ValCurs {
    @XmlElement(name = "Valute")
    private List<Valute> valutes;
}
