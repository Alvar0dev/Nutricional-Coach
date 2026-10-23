package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.MedicionBiometrica;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de mediciones biométricas.
 * Inmutabilidad garantizada por la ausencia de operaciones de borrado/actualización en el servicio.
 */
public interface MedicionBiometricaRepository extends JpaRepository<MedicionBiometrica, String> {
}
