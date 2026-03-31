package com.flexicharge.flexicharge.billing.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "invoices")
public class InvoiceEntity {

    @Id
    private String id;
    private String customerEmail;
    private Double totalKwh;
    private BigDecimal totalAmount;
    private BigDecimal appliedPrice;
    private LocalDateTime createdAt;
    private String pdfPath;
}
