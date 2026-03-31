package com.flexicharge.flexicharge.billing.infrastructure.adapters.out.pdf;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Component
public class OpenPdfAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generateInvoicePdf(InvoiceEntity invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        // Formateadores para que la factura quede "bonita"
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título principal
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("FACTURA FLEXICHARGE", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Espacio en blanco
            document.add(new Paragraph(" "));

            // Datos de la factura
            document.add(new Paragraph("ID Factura: " + invoice.getId()));
            document.add(new Paragraph("Cliente: " + invoice.getCustomerEmail()));
            document.add(new Paragraph("Fecha de emisión: " + invoice.getCreatedAt().format(dateFormat)));
            document.add(new Paragraph("---------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            // Cuerpo de la factura (Consumo y Precios)
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("Energía consumida: " + invoice.getTotalKwh() + " kWh"));

            // Mostramos el precio aplicado (0.50 o 0.20)
            document.add(new Paragraph("Precio unitario: " + currencyFormat.format(invoice.getAppliedPrice()) + " €/kWh"));

            document.add(new Paragraph(" ")); // Separador

            // Total destacado
            Paragraph total = new Paragraph("TOTAL A PAGAR: " + currencyFormat.format(invoice.getTotalAmount()) + " €", fontBold);
            document.add(total);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("---------------------------------------------------------------------------------------"));

            // Pie de página simple
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Paragraph footer = new Paragraph("Gracias por cargar con FlexiCharge. Factura generada automáticamente.", fontSmall);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF con OpenPDF", e);
        }

        return out.toByteArray();
    }
}