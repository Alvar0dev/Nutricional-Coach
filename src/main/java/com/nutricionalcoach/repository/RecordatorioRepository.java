package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA de recordatorios de alerta para pacientes.
 */
public interface RecordatorioRepository extends JpaRepository<Recordatorio, String> {

    /** Devuelve todos los recordatorios con el estado dado. */
    List<Recordatorio> findByEstado(String estado);

    /** Devuelve todos los recordatorios almacenados. */
    List<Recordatorio> findAll();
}
