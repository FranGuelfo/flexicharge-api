package com.flexicharge.flexicharge.plans.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pricing_plans")
public class PricingPlanEntity {

    @Id
    private String id; // Ejemplo: "BASIC", "GOLD"

    private String name;
    private String description;

    // Tarifas del plan
    private BigDecimal pricePunta;
    private BigDecimal priceValle;

    // Podemos añadir un descuento fijo o cuota mensual en el futuro
    private BigDecimal monthlyFee;
}