package com.nutricionalcoach.controller;

import com.nutricionalcoach.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de pacientes.
 * Implementa el endpoint DELETE /api/v1/pacientes/{id} con soft-delete
 * (protección de datos: no elimina físicamente, marca como inactivo).
 */
@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Elimina un paciente (soft-delete). Marca activo=false y fechaBaja.
     *
     * @param id UUID del paciente
     * @return 204 No Content si se eliminó correctamente, 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable String id) {
        boolean eliminado = pacienteService.softDeletePaciente(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
