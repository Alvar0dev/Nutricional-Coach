package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de pacientes.
 * La implementación proviene de Spring Data JPA en tiempo de ejecución.
 */
public interface PacienteRepository extends JpaRepository<Paciente, String> {
}
