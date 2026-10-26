package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;

import java.util.List;

/**
 * Servicio de mediciones biométricas.
 * Las mediciones son inmutables una vez creadas (no hay PUT ni DELETE en la spec).
 */
public interface MedicionBiometricaService {

    /**
     * Registra una nueva medición biométrica para un paciente.
     *
     * @param pacienteId UUID del paciente
     * @param dto        datos de la medición (pesoKg y grasaPct no negativos)
     * @return DTO de la medición registrada
     * @throws IllegalArgumentException si pesoKg o grasaPct son negativos
     */
    MedicionBiometricaDTO registrarMedicion(String pacienteId, MedicionBiometricaDTO dto);

    /**
     * Lista las mediciones biométricas de un paciente.
     *
     * @param pacienteId UUID del paciente
     * @return lista de DTO de mediciones del paciente (vacía si no hay ninguna)
     * @throws IllegalArgumentException si el paciente no existe
     */
    List<MedicionBiometricaDTO> listarMediciones(String pacienteId);
}
