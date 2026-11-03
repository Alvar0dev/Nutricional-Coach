package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.RecordatorioDTO;
import com.nutricionalcoach.dto.RecordatorioFeedbackDTO;
import com.nutricionalcoach.service.RecordatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de recordatorios de alerta.
 * Implementa GET /api/v1/recordatorios (listar pendientes) y
 * PATCH /api/v1/recordatorios/{id} (actualizar feedback del paciente).
 */
@RestController
@RequestMapping("/api/v1/recordatorios")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    public RecordatorioController(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    /**
     * Lista los recordatorios pendientes.
     *
     * @return 200 OK con la lista de RecordatorioDTO pendientes (vacía si no hay ninguno)
     */
    @GetMapping
    public ResponseEntity<List<RecordatorioDTO>> listarRecordatoriosPendientes() {
        List<RecordatorioDTO> recordatorios = recordatorioService.listarRecordatoriosPendientes();
        return ResponseEntity.ok(recordatorios);
    }

    /**
     * Actualiza el feedback del paciente de un recordatorio.
     *
     * @param id UUID del recordatorio
     * @param feedback datos de feedback del paciente a actualizar
     * @return 200 OK con el recordatorio actualizado, 404 Not Found si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RecordatorioDTO> actualizarFeedback(@PathVariable String id,
                                                              @RequestBody RecordatorioFeedbackDTO feedback) {
        return recordatorioService.actualizarFeedback(id, feedback)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
