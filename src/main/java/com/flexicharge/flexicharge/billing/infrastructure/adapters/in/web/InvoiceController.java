package com.flexicharge.flexicharge.billing.infrastructure.adapters.in.web;

import com.flexicharge.flexicharge.billing.application.GenerateInvoiceService;
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

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<InvoiceEntity>> getCustomerInvoices(@PathVariable String email) {
        List<InvoiceEntity> invoices = invoiceService.getInvoicesByCustomer(email);

        if (invoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(invoices);
    }
}
