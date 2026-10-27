package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.PacienteDTO;

import java.util.List;

/**
 * Servicio de pacientes.
 * Toda operación crítica debe ser reversible (soft-delete) para proteger
 * los datos — riesgo 1 de la Constitución (item 1: datos irreversibles).
 */
public interface PacienteService {

    /**
     * Realiza un soft-delete del paciente: establece activo=false y fechaBaja.
     * No elimina registros físicos, preservando la trazabilidad de mediciones
     * y revisiones relacionadas.
     *
     * @param id UUID del paciente
     * @return true si el paciente existió y fue marcado como inactivo, false si no se encontró
     */
    boolean softDeletePaciente(String id);

    /**
     * Crea un nuevo paciente a partir de un DTO de alta. Genera el id y la
     * fecha de alta automáticamente; nunca se acepta un id ya existente.
     *
     * @param pacienteDTO datos del paciente a crear (sin id ni fechaAlta)
     * @return el PacienteDTO creado con id y fechaAlta asignados
     */
    PacienteDTO crearPaciente(PacienteDTO pacienteDTO);

    /**
     * Lista pacientes activos (activo=true). Los pacientes dados de baja
     * (soft-delete) no aparecen en el listado.
     *
     * @param page página a consultar (a partir de 1)
     * @param size cantidad de resultados por página
     * @return lista de DTO de pacientes activos, nunca vacía si hay datos
     */
    List<PacienteDTO> listarPacientesActivos(int page, int size);
}
