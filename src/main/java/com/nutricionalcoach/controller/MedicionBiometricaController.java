package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;
import com.nutricionalcoach.service.MedicionBiometricaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador de mediciones biométricas.
 * Implementa POST /api/v1/pacientes/{id}/mediciones (registro de medición).
 * Las mediciones son inmutables: no hay PUT ni DELETE en la especificación.
 */
@RestController
@RequestMapping("/api/v1/pacientes/{id}/mediciones")
public class MedicionBiometricaController {

    private final MedicionBiometricaService medicionService;

    public MedicionBiometricaController(MedicionBiometricaService medicionService) {
        this.medicionService = medicionService;
    }

    @PostMapping
    public ResponseEntity<MedicionBiometricaDTO> registrarMedicion(
            @PathVariable String id,
            @RequestBody MedicionBiometricaDTO dto) {

        MedicionBiometricaDTO registrada = medicionService.registrarMedicion(id, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{medicionId}")
                .buildAndExpand(registrada.getId())
                .toUri();

        return ResponseEntity.created(location).body(registrada);
    }
}
