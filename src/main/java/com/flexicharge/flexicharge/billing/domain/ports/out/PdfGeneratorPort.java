package com.flexicharge.flexicharge.billing.domain.ports.out;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;

public interface PdfGeneratorPort {
    byte[] generateInvoicePdf(InvoiceEntity invoice);
}
