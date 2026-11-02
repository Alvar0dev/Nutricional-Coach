package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.RecordatorioDTO;
import com.nutricionalcoach.service.RecordatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de recordatorios de alerta.
 * Implementa GET /api/v1/recordatorios (listar recordatorios pendientes).
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
}
