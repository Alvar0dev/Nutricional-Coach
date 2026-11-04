package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.RevisionDTO;
import com.nutricionalcoach.service.RevisionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador de revisiones mensuales.
 * Implementa POST /api/v1/pacientes/{id}/revisiones (crear revisión con deltas)
 * y GET /api/v1/revisiones + GET /api/v1/revisiones/{id} (listar y obtener por ID).
 */
@RestController
@RequestMapping("/api/v1/revisiones")
public class RevisionController {

    private final RevisionService revisionService;

    public RevisionController(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    /**
     * Lista todas las revisiones almacenadas.
     *
     * @return 200 OK con la lista de RevisionDTO
     */
    @GetMapping
    public ResponseEntity<List<RevisionDTO>> listarRevisiones() {
        List<RevisionDTO> revisiones = revisionService.listarRevisiones();
        return ResponseEntity.ok(revisiones);
    }

    /**
     * Obtiene una revisión por su id.
     *
     * @param id UUID de la revisión
     * @return 200 OK con la revisión, 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<RevisionDTO> obtenerRevisionPorId(@PathVariable String id) {
        RevisionDTO revision = revisionService.obtenerRevisionPorId(id);
        if (revision == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(revision);
    }

    /**
     * Crea una nueva revisión mensual para un paciente.
     *
     * @param pacienteId UUID del paciente (path variable)
     * @param dto        datos de la revisión
     * @return 201 Created con la revisión creada y Location header
     */
    @PostMapping("/pacientes/{id}/revisiones")
    public ResponseEntity<RevisionDTO> crearRevision(
            @PathVariable String id,
            @RequestBody RevisionDTO dto) {
        RevisionDTO creada = revisionService.crearRevision(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{revisionId}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * Descarga el PDF del plan de dieta y entreno de una revisión.
     *
     * @param id UUID de la revisión
     * @return 200 OK con el PDF como application/pdf, 404 si no existe
     */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String id) {
        byte[] pdf = revisionService.generarPdfRevision(id);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=revision-" + id + ".pdf")
                .body(pdf);
    }
}
