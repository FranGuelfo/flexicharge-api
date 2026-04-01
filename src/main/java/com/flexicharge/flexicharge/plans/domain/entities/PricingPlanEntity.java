package com.flexicharge.flexicharge.plans.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    private boolean active = true;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;
}