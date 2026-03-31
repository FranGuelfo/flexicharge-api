package com.flexicharge.flexicharge.billing.infrastructure.adapters.out.pdf;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.shared.InfrastructureException;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
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

        // Definición de fuentes al inicio para evitar errores de compilación
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 8);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- CABECERA ---
            Paragraph title = new Paragraph("FACTURA FLEXICHARGE", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // --- DATOS DE FACTURA Y CLIENTE ---
            // Usamos una tabla sin bordes para alinear datos de factura (izquierda) y cliente (derecha)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Celda Izquierda: Info Factura
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.addElement(new Paragraph("ID Factura: " + invoice.getId(), fontSmall));
            infoCell.addElement(new Paragraph("Fecha emisión: " + invoice.getCreatedAt().format(dateFormat), fontSmall));
            headerTable.addCell(infoCell);

            // Celda Derecha: Info Cliente (Usando los nuevos campos de la entidad)
            PdfPCell customerCell = new PdfPCell();
            customerCell.setBorder(Rectangle.NO_BORDER);
            customerCell.addElement(new Paragraph("DATOS DEL CLIENTE", fontBold));
            customerCell.addElement(new Paragraph(invoice.getCustomerFullName()));
            customerCell.addElement(new Paragraph("NIF: " + invoice.getCustomerNif()));
            customerCell.addElement(new Paragraph(invoice.getCustomerAddress()));
            customerCell.addElement(new Paragraph("Email: " + invoice.getCustomerEmail()));
            headerTable.addCell(customerCell);

            document.add(headerTable);
            document.add(new Paragraph("---------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            // --- DETALLE DE LA CARGA ---
            document.add(new Paragraph("DETALLE DE LA SESIÓN", fontSubtitle));
            document.add(new Paragraph(" "));

            PdfPTable detailTable = new PdfPTable(2);
            detailTable.setWidthPercentage(60);
            detailTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            addTableCell(detailTable, "Fecha Inicio:", invoice.getSessionStart().format(dateFormat));
            addTableCell(detailTable, "Fecha Fin:", invoice.getSessionEnd().format(dateFormat));
            addTableCell(detailTable, "Lectura Inicial:", invoice.getInitialKwh() + " kWh");
            addTableCell(detailTable, "Lectura Final:", invoice.getFinalKwh() + " kWh");
            addTableCell(detailTable, "Consumo Total:", invoice.getTotalKwh() + " kWh");
            addTableCell(detailTable, "Precio aplicado:", currencyFormat.format(invoice.getAppliedPrice()) + " €/kWh");

            document.add(detailTable);
            document.add(new Paragraph(" "));

            // --- HISTORIAL (LOG) ---
            document.add(new Paragraph("HISTORIAL DE CARGA (LOG)", fontBold));
            document.add(new Paragraph(" "));

            if (invoice.getHistory() != null && !invoice.getHistory().isEmpty()) {
                PdfPTable logTable = new PdfPTable(2);
                logTable.setWidthPercentage(100);
                logTable.addCell(new Phrase("Hora", fontBold));
                logTable.addCell(new Phrase("Lectura (kWh)", fontBold));

                for (HeartbeatLog logEntry : invoice.getHistory()) {
                    logTable.addCell(logEntry.getTimestamp().format(dateFormat));
                    logTable.addCell(String.valueOf(logEntry.getKwh()));
                }
                document.add(logTable);
            } else {
                document.add(new Paragraph("No se registraron cambios intermedios."));
            }

            document.add(new Paragraph(" "));

            // --- TOTAL ---
            Paragraph total = new Paragraph("TOTAL A PAGAR: " + currencyFormat.format(invoice.getTotalAmount()) + " €", fontSubtitle);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("---------------------------------------------------------------------------------------"));

            // --- PIE DE PÁGINA ---
            Paragraph footer = new Paragraph("Gracias por confiar en FlexiCharge. \nFactura generada electrónicamente conforme a la normativa vigente.", fontSmall);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            log.error("Error técnico generando PDF: {}", e.getMessage());
            throw new InfrastructureException("Error al generar el PDF con OpenPDF");
        }

        return out.toByteArray();
    }

    // Método auxiliar para no repetir código en las tablas
    private void addTableCell(PdfPTable table, String label, String value) {
        table.addCell(new Phrase(label));
        table.addCell(new Phrase(value));
    }
}