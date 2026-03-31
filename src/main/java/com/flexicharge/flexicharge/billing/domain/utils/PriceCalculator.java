package com.flexicharge.flexicharge.billing.domain.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Component
public class PriceCalculator {

    @Value("${billing.prices.punta}")
    private BigDecimal precioPunta;

    @Value("${billing.prices.valle}")
    private BigDecimal precioValle;

    public BigDecimal calculatePrice(OffsetDateTime fecha) {

        LocalTime horaActual = fecha.toLocalTime();
        LocalTime inicioPunta = LocalTime.of(8, 0);
        LocalTime finPunta = LocalTime.of(22, 0);

        // Tarifa punta de 08:00:00 a 21:59:59
        if (!horaActual.isBefore(inicioPunta) && horaActual.isBefore(finPunta)) {
            return precioPunta;
        } else {
            return precioValle;
        }
    }
}
