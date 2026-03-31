package com.flexicharge.flexicharge.billing.infrastructure.adapters.out.email;

import com.flexicharge.flexicharge.billing.domain.ports.out.EmailServicePort;
import com.flexicharge.flexicharge.billing.exceptions.InfrastructureException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailAdapter implements EmailServicePort {

    private final JavaMailSender mailSender;

    @Override
    public void sendInvoiceWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("<h1>Factura FlexiCharge</h1><p>Gracias por usar nuestros cargadores.</p>", true);
            // Adjuntamos el PDF desde el array de bytes
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Fallo al enviar correo a {}: {}", to, e.getMessage());
            throw new InfrastructureException("No se pudo enviar el email con la factura. Por favor, inténtelo más tarde.");
        }
    }
}
