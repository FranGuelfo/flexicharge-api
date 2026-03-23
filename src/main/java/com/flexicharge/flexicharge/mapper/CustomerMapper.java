package com.flexicharge.flexicharge.mapper;

import com.flexicharge.flexicharge.model.Customer;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Si el nombre del campo en el DTO es "planName"
    // y en la Entidad está dentro de "subscription.planName"
    @Mapping(source = "subscription.planName", target = "planName")
    CustomerDto toDTO(Customer customer);

    // Al convertir a Entidad, ignoramos la suscripción porque
    // la lógica de negocio (el Service) se encargará de buscar el Plan en la BD
    @Mapping(target = "subscription", ignore = true)
    Customer toEntity(CustomerDto dto);
}
