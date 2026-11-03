package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.RecordatorioDTO;
import com.nutricionalcoach.dto.RecordatorioFeedbackDTO;
import com.nutricionalcoach.service.RecordatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecordatorioControllerTest {

    @Test
    void listarRecordatoriosPendientes_retorna200ConLista() {
        RecordatorioService service = mock(RecordatorioService.class);
        RecordatorioDTO r1 = new RecordatorioDTO(
                "rec-1", "paciente-1", LocalDate.of(2024, 8, 1),
                "CONTROL", "PENDIENTE", null);
        RecordatorioDTO r2 = new RecordatorioDTO(
                "rec-2", "paciente-2", LocalDate.of(2024, 8, 5),
                "REVISION_MENSUAL", "PENDIENTE", "Me siento bien");
        when(service.listarRecordatoriosPendientes()).thenReturn(List.of(r1, r2));

        RecordatorioController controller = new RecordatorioController(service);

        ResponseEntity<List<RecordatorioDTO>> respuesta = controller.listarRecordatoriosPendientes();

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody()).hasSize(2);
        assertThat(respuesta.getBody().get(0).getId()).isEqualTo("rec-1");
        assertThat(respuesta.getBody().get(0).getEstado()).isEqualTo("PENDIENTE");
        assertThat(respuesta.getBody().get(1).getId()).isEqualTo("rec-2");
        verify(service, times(1)).listarRecordatoriosPendientes();
    }

    @Test
    void actualizarFeedback_retorna200ConRecordatorioActualizado() {
        RecordatorioService service = mock(RecordatorioService.class);
        RecordatorioFeedbackDTO feedback = new RecordatorioFeedbackDTO("Me siento con más energía");
        RecordatorioDTO actualizado = new RecordatorioDTO(
                "rec-1", "paciente-1", LocalDate.of(2024, 8, 1),
                "CONTROL", "PENDIENTE", "Me siento con más energía");
        when(service.actualizarFeedback("rec-1", feedback)).thenReturn(Optional.of(actualizado));

        RecordatorioController controller = new RecordatorioController(service);

        ResponseEntity<RecordatorioDTO> respuesta = controller.actualizarFeedback("rec-1", feedback);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo("rec-1");
        assertThat(respuesta.getBody().getFeedbackPaciente()).isEqualTo("Me siento con más energía");
        verify(service, times(1)).actualizarFeedback("rec-1", feedback);
    }

    @Test
    void actualizarFeedback_retorna404CuandoNoExiste() {
        RecordatorioService service = mock(RecordatorioService.class);
        RecordatorioFeedbackDTO feedback = new RecordatorioFeedbackDTO("Feedback");
        when(service.actualizarFeedback("rec-inexistente", feedback)).thenReturn(Optional.empty());

        RecordatorioController controller = new RecordatorioController(service);

        ResponseEntity<RecordatorioDTO> respuesta = controller.actualizarFeedback("rec-inexistente", feedback);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        assertThat(respuesta.getBody()).isNull();
        verify(service, times(1)).actualizarFeedback("rec-inexistente", feedback);
    }
}
