package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RecordatorioDTO;
import com.nutricionalcoach.dto.RecordatorioFeedbackDTO;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de recordatorios de alerta para pacientes.
 * Permite listar los recordatorios pendientes (estado PENDIENTE) y actualizar
 * el feedback del paciente de un recordatorio existente.
 */
public interface RecordatorioService {

    /**
     * Lista los recordatorios pendientes.
     *
     * @return lista de RecordatorioDTO con estado PENDIENTE (vacía si no hay ninguno)
     */
    List<RecordatorioDTO> listarRecordatoriosPendientes();

    /**
     * Actualiza el feedback del paciente de un recordatorio existente.
     *
     * @param id UUID del recordatorio
     * @param feedback datos de feedback del paciente a actualizar
     * @return Optional con el RecordatorioDTO actualizado, o vacío si no existe el recordatorio
     */
    Optional<RecordatorioDTO> actualizarFeedback(String id, RecordatorioFeedbackDTO feedback);
}
