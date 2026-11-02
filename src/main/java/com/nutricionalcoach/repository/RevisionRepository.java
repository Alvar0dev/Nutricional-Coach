package com.nutricionalcoach.repository;

import com.nutricionalcoach.model.Revision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA de revisiones mensuales.
 */
public interface RevisionRepository extends JpaRepository<Revision, String> {

    /** Devuelve todas las revisiones almacenadas. */
    List<Revision> findAll();

    /** Devuelve la revisión con el id dado, si existe. */
    java.util.Optional<Revision> findById(String id);
}
