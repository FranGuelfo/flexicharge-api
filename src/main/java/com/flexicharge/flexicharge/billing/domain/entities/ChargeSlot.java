package com.flexicharge.flexicharge.billing.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class ChargeSlot {
    private OffsetDateTime intervalStart;
    private OffsetDateTime intervalEnd;
    private Double kwhConsumed;
    private Double priceAtThatTime;
    private BigDecimal subtotal;
}
