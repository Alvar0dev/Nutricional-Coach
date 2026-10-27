package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.PacienteDTO;
import com.nutricionalcoach.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;

/**
 * Controlador de pacientes.
 * Implementa el endpoint DELETE /api/v1/pacientes/{id} con soft-delete
 * (protección de datos: no elimina físicamente, marca como inactivo) y el
 * endpoint GET /api/v1/pacientes que lista solo pacientes activos.
 */
@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Lista pacientes activos (soft-delete: dados de baja no aparecen).
     *
     * @param page página a consultar (a partir de 1)
     * @param size cantidad de resultados por página
     * @return 200 OK con la lista de pacientes activos
     */
    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarPacientes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<PacienteDTO> pacientes = pacienteService.listarPacientesActivos(page, size);
        return ResponseEntity.ok(pacientes);
    }

    /**
     * Elimina un paciente (soft-delete). Marca activo=false y fechaBaja.
     *
     * @param id UUID del paciente
     * @return 204 No Content si se eliminó correctamente, 404 Not Found si no existe
     */
    /**
     * Crea un nuevo paciente.
     *
     * @param pacienteDTO datos del paciente a crear (sin id ni fechaAlta)
     * @return 201 Created con el paciente creado y Location header, o 400 si falta nombre o email
     */
    @PostMapping
    public ResponseEntity<PacienteDTO> crearPaciente(@RequestBody PacienteDTO pacienteDTO) {
        if (pacienteDTO.getNombre() == null || pacienteDTO.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (pacienteDTO.getEmail() == null || pacienteDTO.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        PacienteDTO creado = pacienteService.crearPaciente(pacienteDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable String id) {
        boolean eliminado = pacienteService.softDeletePaciente(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
