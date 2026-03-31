package com.flexicharge.flexicharge.billing.infrastructure.adapters.in.web;

import com.flexicharge.flexicharge.billing.application.usecases.GenerateInvoiceService;
import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final GenerateInvoiceService invoiceService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateInvoice(@RequestParam String email, @RequestParam Double kwh) {
        byte[] pdfBytes = invoiceService.createAndSaveInvoice(email, kwh);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura_" + email + ".pdf\"")
                .body(pdfBytes);
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<InvoiceEntity>> getCustomerInvoices(@PathVariable String email) {
        List<InvoiceEntity> invoices = invoiceService.getInvoicesByCustomer(email);

        if (invoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(invoices);
    }
}
