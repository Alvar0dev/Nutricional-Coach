package com.nutricionalcoach.service;

import com.nutricionalcoach.model.Paciente;
import com.nutricionalcoach.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio de pacientes con soft-delete obligatorio.
 * Toda eliminación marca activo=false y fechaBaja; nunca elimina físicamente.
 */
@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional
    public boolean softDeletePaciente(String id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isEmpty()) {
            return false;
        }
        Paciente p = paciente.get();
        p.softDelete();
        pacienteRepository.save(p);
        return true;
    }
}
