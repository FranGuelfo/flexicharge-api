package com.flexicharge.flexicharge.billing.domain.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class PriceCalculator {

    @Value("${billing.prices.punta}")
    private BigDecimal precioPunta;

    @Value("${billing.prices.valle}")
    private BigDecimal precioValle;

    public BigDecimal calcularPrecioSegunHora(LocalDateTime fecha) {
        LocalTime horaActual = fecha.toLocalTime();
        LocalTime inicioPunta = LocalTime.of(8, 0);
        LocalTime finPunta = LocalTime.of(22, 0);

        if (horaActual.isAfter(inicioPunta) && horaActual.isBefore(finPunta)) {
            return precioPunta;
        }
        return precioValle;
    }
}
