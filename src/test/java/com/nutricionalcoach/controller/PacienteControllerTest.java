package com.nutricionalcoach.controller;

import com.nutricionalcoach.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
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
}
