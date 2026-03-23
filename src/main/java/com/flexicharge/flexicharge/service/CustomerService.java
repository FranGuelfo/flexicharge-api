package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.dto.CustomerDto;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> findAll();

    CustomerDto findById(String id);

    CustomerDto findByEmail(String email);

    CustomerDto save(CustomerDto customer);

    void delete(String id);
}
