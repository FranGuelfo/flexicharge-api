package com.flexicharge.flexicharge.charging.domain.utils;

import org.springframework.stereotype.Component;

@Component
public class EnergyCalculator {

    public Double calculateConsumedEnergy(Double initial, Double current) {
        if (current < initial) {
            // Caso: El contador del cargador se ha reseteado a cero
            // (Ejemplo simple: asumimos que el current es lo nuevo consumido tras el reset)
            return current;
        }
        return current - initial;
    }
}
