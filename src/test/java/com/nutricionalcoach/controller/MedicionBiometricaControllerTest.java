package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;
import com.nutricionalcoach.service.MedicionBiometricaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MedicionBiometricaControllerTest {

    @Test
    void registrarMedicionValida_retorna201() {
        MedicionBiometricaService service = mock(MedicionBiometricaService.class);
        MedicionBiometricaDTO dtoEntrada = new MedicionBiometricaDTO(
                null, "paciente-1", LocalDate.of(2024, 6, 1),
                75.0, 18.5, 62.0, 85.0);
        MedicionBiometricaDTO dtoSalida = new MedicionBiometricaDTO(
                "med-1", "paciente-1", LocalDate.of(2024, 6, 1),
                75.0, 18.5, 62.0, 85.0);
        when(service.registrarMedicion("paciente-1", dtoEntrada)).thenReturn(dtoSalida);

        MedicionBiometricaController controller = new MedicionBiometricaController(service);

        ResponseEntity<MedicionBiometricaDTO> respuesta =
                controller.registrarMedicion("paciente-1", dtoEntrada);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo("med-1");
        assertThat(respuesta.getHeaders().getLocation()).isNotNull();
        assertThat(respuesta.getHeaders().getLocation().toString()).contains("med-1");
        verify(service, times(1)).registrarMedicion("paciente-1", dtoEntrada);
    }

    @Test
    void registrarMedicionPacienteInexistente_propagaExcepcion() {
        MedicionBiometricaService service = mock(MedicionBiometricaService.class);
        when(service.registrarMedicion(anyString(), any()))
                .thenThrow(new IllegalArgumentException("Paciente no encontrado"));

        MedicionBiometricaController controller = new MedicionBiometricaController(service);

        try {
            controller.registrarMedicion("paciente-999", new MedicionBiometricaDTO());
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Paciente no encontrado");
        }
        verify(service, times(1)).registrarMedicion("paciente-999", any());
    }
}
