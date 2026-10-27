package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.PacienteDTO;
import com.nutricionalcoach.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test para validar la protección de soft-delete en DELETE /api/v1/pacientes/{id}.
 *
 * <p>Cubre los casos descritos en el flujo de negocio del paciente:</p>
 * <ul>
 *   <li>Paciente existente → 204 No Content</li>
 *   <li>Paciente inexistente → 404 Not Found</li>
 *   <li>soft-delete invocado una vez por eliminación</li>
 * </ul>
 */
class PacienteControllerTest {

    @Test
    void deletePacienteExistente_retorna204() {
        PacienteService service = mock(PacienteService.class);
        when(service.softDeletePaciente(anyString())).thenReturn(true);

        PacienteController controller = new PacienteController(service);

        ResponseEntity<Void> respuesta = controller.deletePaciente("123e4567-e89b-12d3-a456-426614174000");

        assertThat(respuesta.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(respuesta.getStatusCode().value()).isEqualTo(204);
        verify(service, times(1)).softDeletePaciente(anyString());
    }

    @Test
    void deletePacienteInexistente_retorna404() {
        PacienteService service = mock(PacienteService.class);
        when(service.softDeletePaciente(anyString())).thenReturn(false);

        PacienteController controller = new PacienteController(service);

        ResponseEntity<Void> respuesta = controller.deletePaciente("999e4567-e89b-12d3-a456-426614174000");

        assertThat(respuesta.getStatusCode().is4xxClientError()).isTrue();
        assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        verify(service, times(1)).softDeletePaciente(anyString());
    }

    @Test
    void listarPacientes_retorna200ConSoloActivos() {
        PacienteService service = mock(PacienteService.class);
        PacienteDTO activo = new PacienteDTO("1", "Juan", "Pérez", "juan@test.com",
                "123456789", "perder peso", LocalDate.of(2024, 1, 1));
        when(service.listarPacientesActivos(1, 20)).thenReturn(List.of(activo));

        PacienteController controller = new PacienteController(service);

        ResponseEntity<List<PacienteDTO>> respuesta = controller.listarPacientes(1, 20);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).hasSize(1);
        assertThat(respuesta.getBody().get(0).getId()).isEqualTo("1");
        assertThat(respuesta.getBody().get(0).getNombre()).isEqualTo("Juan");
        verify(service, times(1)).listarPacientesActivos(1, 20);
    }

    @Test
    void listarPacientes_pacienteEliminadoNoAparece_retorna200ListaVacia() {  // S4
        PacienteService service = mock(PacienteService.class);
        when(service.listarPacientesActivos(1, 20)).thenReturn(List.of());

        PacienteController controller = new PacienteController(service);

        ResponseEntity<List<PacienteDTO>> respuesta = controller.listarPacientes(1, 20);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isEmpty();
        verify(service, times(1)).listarPacientesActivos(1, 20);
    }

    @Test
    void crearPaciente_valido_retorna201ConIdYFechaAlta() {
        PacienteService service = mock(PacienteService.class);
        PacienteDTO creado = new PacienteDTO("1", "Juan", "Pérez", "juan@test.com",
                "123456789", "perder peso", LocalDate.of(2024, 1, 1));
        when(service.crearPaciente(any(PacienteDTO.class))).thenReturn(creado);

        PacienteController controller = new PacienteController(service);
        PacienteDTO entrada = new PacienteDTO(null, "Juan", "Pérez", "juan@test.com",
                "123456789", "perder peso", null);

        ResponseEntity<PacienteDTO> respuesta = controller.crearPaciente(entrada);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
        assertThat(respuesta.getBody().getId()).isEqualTo("1");
        assertThat(respuesta.getBody().getFechaAlta()).isEqualTo(LocalDate.of(2024, 1, 1));
        verify(service, times(1)).crearPaciente(entrada);
    }

    @Test
    void crearPaciente_sinNombre_retorna400yNoLlamaAlServicio() {
        PacienteService service = mock(PacienteService.class);
        PacienteController controller = new PacienteController(service);
        PacienteDTO entrada = new PacienteDTO(null, null, "Pérez", "juan@test.com",
                "123456789", "perder peso", null);

        ResponseEntity<PacienteDTO> respuesta = controller.crearPaciente(entrada);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        verify(service, never()).crearPaciente(any());
    }

    @Test
    void crearPaciente_sinEmail_retorna400yNoLlamaAlServicio() {
        PacienteService service = mock(PacienteService.class);
        PacienteController controller = new PacienteController(service);
        PacienteDTO entrada = new PacienteDTO(null, "Juan", "Pérez", "",
                "123456789", "perder peso", null);

        ResponseEntity<PacienteDTO> respuesta = controller.crearPaciente(entrada);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
        verify(service, never()).crearPaciente(any());
    }
}
