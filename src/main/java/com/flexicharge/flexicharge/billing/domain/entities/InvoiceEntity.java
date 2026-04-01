package com.flexicharge.flexicharge.billing.domain.entities;

import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private OffsetDateTime sessionStart;
    private OffsetDateTime sessionEnd;
    private Double initialKwh;
    private Double finalKwh;
    private String pdfPath;
    private List<ChargeSlot> details;
    private String customerFullName;
    private String customerNif;
    private String customerAddress;

    @Builder.Default
    private List<HeartbeatLog> history = new ArrayList<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;
}
