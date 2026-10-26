package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.MedicionBiometrica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA de mediciones biométricas.
 * Inmutabilidad garantizada por la ausencia de operaciones de borrado/actualización en el servicio.
 */
public interface MedicionBiometricaRepository extends JpaRepository<MedicionBiometrica, String> {

    /** Devuelve las mediciones biométricas de un paciente, identificadas por pacienteId. */
    List<MedicionBiometrica> findByPacienteId(String pacienteId);
}
