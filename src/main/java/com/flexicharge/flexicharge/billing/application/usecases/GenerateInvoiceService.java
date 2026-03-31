package com.flexicharge.flexicharge.billing.application.usecases;

import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.billing.domain.utils.PriceCalculator;
import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.infrastructure.adapters.out.persistence.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateInvoiceService {

    private final InvoiceRepository repository;
    private final PdfGeneratorPort pdfGenerator;
    private final PriceCalculator priceCalculator;

    public byte[] createAndSaveInvoice(String email, Double kwh) {
        // --- VALIDACIÓN ---
        if (kwh == null || kwh <= 0) {
            log.error("Intento de factura con kWh inválido: {}", kwh);
            throw new IllegalArgumentException("El consumo de energía debe ser mayor que cero.");
        }

        LocalDateTime ahora = LocalDateTime.now();

        // Usamos el método del Bean inyectado
        BigDecimal precioAplicado = priceCalculator.calcularPrecioSegunHora(ahora);

        BigDecimal totalAmount = precioAplicado.multiply(BigDecimal.valueOf(kwh))
                .setScale(2, RoundingMode.HALF_UP);

        String fileName = "factura_" + email + "_" + System.currentTimeMillis() + ".pdf";

        InvoiceEntity invoice = InvoiceEntity.builder()
                .customerEmail(email)
                .totalKwh(kwh)
                .appliedPrice(precioAplicado)
                .totalAmount(totalAmount)
                .createdAt(ahora)
                .pdfPath(fileName)
                .build();

        log.info("Guardando factura: {} kWh a {} €/kWh", kwh, precioAplicado);

        InvoiceEntity savedInvoice = repository.save(invoice);
        return pdfGenerator.generateInvoicePdf(savedInvoice);
    }

    public List<InvoiceEntity> getInvoicesByCustomer(String email) {
        log.info("Buscando facturas en Mongo para el cliente: [{}]", email);
        return repository.findByCustomerEmail(email);
    }
}
