package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;
import com.nutricionalcoach.service.MedicionBiometricaService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

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
        verify(service, times(1)).registrarMedicion(eq("paciente-999"), any());
    }

    @Test
    void listarMedicionesPacienteExistente_retorna200ConLista() {
        MedicionBiometricaService service = mock(MedicionBiometricaService.class);
        MedicionBiometricaDTO m1 = new MedicionBiometricaDTO(
                "med-1", "paciente-1", LocalDate.of(2024, 6, 1),
                75.0, 18.5, 62.0, 85.0);
        MedicionBiometricaDTO m2 = new MedicionBiometricaDTO(
                "med-2", "paciente-1", LocalDate.of(2024, 7, 1),
                74.0, 18.0, 62.5, 84.0);
        when(service.listarMediciones("paciente-1")).thenReturn(List.of(m1, m2));

        MedicionBiometricaController controller = new MedicionBiometricaController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<List<MedicionBiometricaDTO>> respuesta =
                controller.listarMediciones("paciente-1");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody()).hasSize(2);
        assertThat(respuesta.getBody().get(0).getId()).isEqualTo("med-1");
        assertThat(respuesta.getBody().get(1).getId()).isEqualTo("med-2");
        verify(service, times(1)).listarMediciones("paciente-1");
    }

    @Test
    void listarMedicionesPacienteInexistente_retorna404() {
        MedicionBiometricaService service = mock(MedicionBiometricaService.class);
        when(service.listarMediciones("paciente-999"))
                .thenThrow(new IllegalArgumentException("Paciente no encontrado"));

        MedicionBiometricaController controller = new MedicionBiometricaController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        try {
            controller.listarMediciones("paciente-999");
            throw new AssertionError("debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Paciente no encontrado");
        }
        verify(service, times(1)).listarMediciones("paciente-999");
    }
}
