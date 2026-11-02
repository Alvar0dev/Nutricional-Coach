package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RecordatorioDTO;

import java.util.List;

/**
 * Servicio de recordatorios de alerta para pacientes.
 * Permite listar los recordatorios pendientes (estado PENDIENTE).
 */
public interface RecordatorioService {

    /**
     * Lista los recordatorios pendientes.
     *
     * @return lista de RecordatorioDTO con estado PENDIENTE (vacía si no hay ninguno)
     */
    List<RecordatorioDTO> listarRecordatoriosPendientes();
}
