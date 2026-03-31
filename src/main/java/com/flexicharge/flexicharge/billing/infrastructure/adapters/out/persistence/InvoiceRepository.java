package com.flexicharge.flexicharge.billing.infrastructure.adapters.out.persistence;

import com.flexicharge.flexicharge.billing.domain.entities.InvoiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<InvoiceEntity, String> {

    List<InvoiceEntity> findByCustomerEmail(String email);
}
