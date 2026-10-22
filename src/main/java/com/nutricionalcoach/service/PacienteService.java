package com.nutricionalcoach.service;

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
}
