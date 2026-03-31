package com.flexicharge.flexicharge.assets.infrastructure;

import com.flexicharge.flexicharge.assets.application.ChargerService;
import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets/chargers")
@RequiredArgsConstructor
public class ChargerController {
    private final ChargerService service;

    @GetMapping
    public ResponseEntity<List<ChargerEntity>> listAll() {
        return ResponseEntity.ok(service.getAllChargers());
    }

    @PostMapping
    public ResponseEntity<ChargerEntity> save(@RequestBody ChargerEntity charger) {
        return ResponseEntity.ok(service.createOrUpdateCharger(charger));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteCharger(id);
        return ResponseEntity.noContent().build();
    }
}
