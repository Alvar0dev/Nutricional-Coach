package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RevisionDTO;

import java.util.List;

/**
 * Servicio de revisiones mensuales.
 * Una revisión compara dos mediciones biométricas de un paciente y calcula
 * los deltas de peso, grasa y masa muscular.
 */
public interface RevisionService {

    /**
     * Crea una nueva revisión mensual con los deltas calculados.
     *
     * @param dto datos de la revisión (pacienteId, mediciones, deltas, ajustes)
     * @return el RevisionDTO creado con id y fechaRevision asignados
     * @throws IllegalArgumentException si el paciente no existe
     */
    RevisionDTO crearRevision(RevisionDTO dto);

    /**
     * Lista todas las revisiones almacenadas.
     *
     * @return lista de RevisionDTO (vacía si no hay ninguna)
     */
    List<RevisionDTO> listarRevisiones();

    /**
     * Obtiene una revisión por su id.
     *
     * @param id UUID de la revisión
     * @return el RevisionDTO si existe, null si no se encuentra
     */
    RevisionDTO obtenerRevisionPorId(String id);

    /**
     * Genera el PDF del plan de dieta y entreno de una revisión.
     *
     * @param id UUID de la revisión
     * @return los bytes del PDF, o null si la revisión no existe
     */
    byte[] generarPdfRevision(String id);
}
