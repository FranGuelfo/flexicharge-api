package com.flexicharge.flexicharge.billing.infrastructure.adapters.out.pdf;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.billing.exceptions.InfrastructureException;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class OpenPdfAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generateInvoicePdf(InvoiceEntity invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

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

            // Cuerpo de la factura
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("DETALLE DE LA CARGA", fontBold));
            document.add(new Paragraph("---------------------------"));
            document.add(new Paragraph("Fecha Inicio: " + invoice.getSessionStart().format(dateFormat)));
            document.add(new Paragraph("Fecha Fin:    " + invoice.getSessionEnd().format(dateFormat)));
            document.add(new Paragraph("Lectura Inicial: " + invoice.getInitialKwh() + " kWh"));
            document.add(new Paragraph("Lectura Final:   " + invoice.getFinalKwh() + " kWh"));
            document.add(new Paragraph("Consumo Total:   " + invoice.getTotalKwh() + " kWh"));
            document.add(new Paragraph("Precio aplicado: " + currencyFormat.format(invoice.getAppliedPrice()) + " €/kWh"));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("HISTORIAL DE CARGA (LOG)", fontBold));
            document.add(new Paragraph(" "));

            if (invoice.getHistory() != null && !invoice.getHistory().isEmpty()) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.addCell("Hora");
                table.addCell("Lectura (kWh)");

                for (HeartbeatLog log : invoice.getHistory()) {
                    table.addCell(log.getTimestamp().format(dateFormat));
                    table.addCell(String.valueOf(log.getKwh()));
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No se registraron cambios de lectura durante la sesión."));
            }

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
            log.error("Error técnico generando PDF: {}", e.getMessage());
            throw new InfrastructureException("Error al generar el PDF con OpenPDF");
        }

        return out.toByteArray();
    }
}