package com.flexicharge.flexicharge.billing.domain.ports.out;

public interface EmailServicePort {
    void sendInvoiceWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName);
}
