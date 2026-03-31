package com.flexicharge.flexicharge.billing.application;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.EmailServicePort;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.billing.domain.utils.PriceCalculator;
import com.flexicharge.flexicharge.billing.infrastructure.adapters.out.persistence.InvoiceRepository;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateInvoiceService {

    private final InvoiceRepository repository;
    private final PdfGeneratorPort pdfGenerator;
    private final PriceCalculator priceCalculator;
    private final EmailServicePort emailService;

    public byte[] createAndSaveInvoice(String email, Double kwh, OffsetDateTime start,
                                       OffsetDateTime end, Double initial, Double finalK,
                                       List<HeartbeatLog> history) {
        if (kwh == null || kwh <= 0) {
            log.error("Intento de factura con kWh inválido: {}", kwh);
            throw new IllegalArgumentException("El consumo de energía debe ser mayor que cero.");
        }

        OffsetDateTime dateNow = OffsetDateTime.now(ZoneOffset.UTC);

        BigDecimal precioApplicator = priceCalculator.calculatePrice(dateNow);

        BigDecimal totalAmount = precioApplicator.multiply(BigDecimal.valueOf(kwh))
                .setScale(2, RoundingMode.HALF_UP);

        String fileName = "factura_" + email + "_" + System.currentTimeMillis() + ".pdf";

        InvoiceEntity invoice = InvoiceEntity.builder()
                .customerEmail(email)
                .totalKwh(kwh)
                .appliedPrice(precioApplicator)
                .sessionStart(start)
                .sessionEnd(end)
                .initialKwh(initial)
                .finalKwh(finalK)
                .totalAmount(totalAmount)
                .createdAt(dateNow)
                .pdfPath(fileName)
                .history(history != null ? history : new ArrayList<>())
                .build();

        log.info("Guardando factura: {} kWh a {} €/kWh", kwh, precioApplicator);

        InvoiceEntity savedInvoice = repository.save(invoice);
        byte[] pdfBytes = pdfGenerator.generateInvoicePdf(savedInvoice);

        // ENVIAR EMAIL
        emailService.sendInvoiceWithAttachment(
                email,
                "Tu Factura de Carga FlexiCharge",
                "Hola, adjuntamos la factura de tu última sesión de carga. ¡Gracias!",
                pdfBytes,
                fileName
        );

        return pdfBytes;
    }

    public List<InvoiceEntity> getInvoicesByCustomer(String email) {
        log.info("Buscando facturas en Mongo para el cliente: [{}]", email);
        return repository.findByCustomerEmail(email);
    }
}
