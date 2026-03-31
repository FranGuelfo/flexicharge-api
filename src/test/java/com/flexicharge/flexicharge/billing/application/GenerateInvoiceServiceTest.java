package com.flexicharge.flexicharge.billing.application;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import com.flexicharge.flexicharge.billing.domain.ports.out.PdfGeneratorPort;
import com.flexicharge.flexicharge.billing.domain.utils.PriceCalculator;
import com.flexicharge.flexicharge.billing.infrastructure.adapters.out.persistence.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateInvoiceServiceTest {

    @Mock
    private InvoiceRepository repository;

    @Mock
    private PdfGeneratorPort pdfGenerator;

    @Mock
    private PriceCalculator priceCalculator;

    @InjectMocks
    private GenerateInvoiceService service;

//    @Test
//    void whenKwhIsNegative_shouldThrowException() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            service.createAndSaveInvoice("test@test.com", -10.0, OffsetDateTime.now(), OffsetDateTime.now(), 1.0, 2.0);
//        });
//    }
//
//    @Test
//    void createAndSaveInvoice_shouldReturnPdfBytes() {
//        // Arrange
//        String email = "fran@test.com";
//        Double kwh = 20.0;
//        BigDecimal price = new BigDecimal("0.50");
//        byte[] expectedPdf = new byte[]{1, 2, 3};
//
//        when(priceCalculator.calcularPrecioSegunHora(any())).thenReturn(price);
//        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//        when(pdfGenerator.generateInvoicePdf(any())).thenReturn(expectedPdf);
//
//        // Act
//        byte[] result = service.createAndSaveInvoice(email, kwh);
//
//        // Assert
//        assertArrayEquals(expectedPdf, result);
//        verify(repository).save(argThat(invoice ->
//                invoice.getTotalAmount().compareTo(new BigDecimal("10.00")) == 0 &&
//                        invoice.getCustomerEmail().equals("fran@test.com")
//        ));
//    }
//
//    @Test
//    void whenKwhIsZero_shouldThrowException() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            service.createAndSaveInvoice("test@test.com", 0.0);
//        });
//    }

    @Test
    void getInvoicesByCustomer_shouldReturnList() {
        // Arrange
        String email = "fran@test.com";
        List<InvoiceEntity> mockList = List.of(
                InvoiceEntity.builder().customerEmail(email).totalAmount(new BigDecimal("10.00")).build(),
                InvoiceEntity.builder().customerEmail(email).totalAmount(new BigDecimal("5.00")).build()
        );
        when(repository.findByCustomerEmail(email)).thenReturn(mockList);

        // Act
        List<InvoiceEntity> result = service.getInvoicesByCustomer(email);

        // Assert
        assertEquals(2, result.size());
        assertEquals(email, result.get(0).getCustomerEmail());
        verify(repository, times(1)).findByCustomerEmail(email);
    }
}
