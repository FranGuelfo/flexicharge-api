package com.flexicharge.flexicharge.billing.application;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.EmailServicePort;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.plans.application.PriceCalculator;
import com.flexicharge.flexicharge.shared.exception.InfrastructureException;
import com.flexicharge.flexicharge.billing.infrastructure.adapters.out.persistence.InvoiceRepository;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import com.flexicharge.flexicharge.shared.Address;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerEntity;
import com.flexicharge.flexicharge.identity.domain.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    public byte[] createAndSaveInvoice(String email, Double kwh, OffsetDateTime start,
                                       OffsetDateTime end, Double initial, Double finalK,
                                       List<HeartbeatLog> history) {

        // 1. Buscamos los datos del cliente (Si no existe, usamos valores por defecto o lanzamos error)
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new InfrastructureException("No se pueden generar facturas para clientes sin perfil completo."));

        BigDecimal precioAplicado = priceCalculator.calculatePrice(end, customer.getPlanId());
        BigDecimal totalAmount = precioAplicado.multiply(BigDecimal.valueOf(kwh)).setScale(2, RoundingMode.HALF_UP);

        // 2. Construimos la factura con los datos FISCALES del momento
        InvoiceEntity invoice = InvoiceEntity.builder()
                .customerEmail(email)
                .customerFullName(customer.getFirstName() + " " + customer.getLastName())
                .customerNif(customer.getNif())
                .customerAddress(formatAddress(customer.getAddress()))
                .totalKwh(kwh)
                .appliedPrice(precioAplicado)
                .sessionStart(start)
                .sessionEnd(end)
                .initialKwh(initial)
                .finalKwh(finalK)
                .history(history != null ? history : new ArrayList<>())
                .totalAmount(totalAmount)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        log.info("Generando factura legal para: {}", invoice.getCustomerFullName());

        InvoiceEntity savedInvoice = repository.save(invoice);
        byte[] pdfBytes = pdfGenerator.generateInvoicePdf(savedInvoice);

        // Envío de email (mantenemos tu lógica)
        emailService.sendInvoiceWithAttachment(email, "Factura FlexiCharge", "Adjuntamos tu factura.", pdfBytes, "factura.pdf");

        return pdfBytes;
    }

    private String formatAddress(Address addr) {
        return String.format("%s, %s (%s), %s", addr.getStreet(), addr.getCity(), addr.getZipCode(), addr.getCountry());
    }


    public List<InvoiceEntity> getInvoicesByCustomer(String email) {
        log.info("Buscando facturas en Mongo para el cliente: [{}]", email);
        return repository.findByCustomerEmail(email);
    }
}
