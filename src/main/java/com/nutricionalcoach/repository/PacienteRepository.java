package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA de pacientes.
 * La implementación proviene de Spring Data JPA en tiempo de ejecución.
 */
public interface PacienteRepository extends JpaRepository<Paciente, String> {

    /**
     * Devuelve solo los pacientes activos (soft-delete: activos=true nunca se listan como dados de baja).
     */
    List<Paciente> findByActivoTrue();
}
