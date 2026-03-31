package com.flexicharge.flexicharge.charging.infrastructure.adapters.in.rest;

import com.flexicharge.flexicharge.charging.application.ChargingSessionService;
import com.flexicharge.flexicharge.charging.application.dtos.ActiveSessionDTO;
import com.flexicharge.flexicharge.charging.domain.entities.ChargingSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ChargingSessionController {

    private final ChargingSessionService service;

    @PostMapping("/start")
    public ResponseEntity<ChargingSession> start(@RequestParam String email,
                                                 @RequestParam String chargerId,
                                                 @RequestParam Double initialKwh) {
        return ResponseEntity.ok(service.startSession(email, chargerId, initialKwh));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<String> stop(@PathVariable String id, @RequestParam Double finalKwh) {
        service.stopSession(id, finalKwh);
        return ResponseEntity.ok("Sesión finalizada. La factura ha sido enviada a su correo.");
    }

    @PatchMapping("/{id}/heartbeat")
    public ResponseEntity<ChargingSession> heartbeat(
            @PathVariable String id,
            @RequestParam Double currentKwh) {

        ChargingSession updatedSession = service.updateHeartbeat(id, currentKwh);
        return ResponseEntity.ok(updatedSession);
    }

    @GetMapping("/active/{email}")
    public ResponseEntity<ActiveSessionDTO> getActive(@PathVariable String email) {
        return ResponseEntity.ok(service.getActiveSession(email));
    }
}
