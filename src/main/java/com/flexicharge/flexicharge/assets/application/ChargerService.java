package com.flexicharge.flexicharge.assets.application;

import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import com.flexicharge.flexicharge.assets.domain.repository.ChargerRepository;
import com.flexicharge.flexicharge.shared.exception.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargerService {
    private final ChargerRepository repository;

    public ChargerEntity createOrUpdateCharger(ChargerEntity charger) {
        log.info("Registrando/Actualizando cargador: {}", charger.getId());
        // Si es nuevo, aseguramos que empiece como AVAILABLE
        if (charger.getStatus() == null) {
            charger.setStatus("AVAILABLE");
        }
        return repository.save(charger);
    }

    public void deleteCharger(String id) {
        ChargerEntity charger = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new InfrastructureException("No se puede eliminar: El cargador no existe o ya está inactivo."));

        log.warn("Realizando Soft Delete del cargador: {}", id);

        charger.setActive(false);
        repository.save(charger);
    }

    public List<ChargerEntity> getAllChargers() {
        return repository.findAllByActiveTrue();
    }
}
