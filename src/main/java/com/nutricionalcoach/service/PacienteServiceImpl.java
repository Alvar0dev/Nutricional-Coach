package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.PacienteDTO;
import com.nutricionalcoach.model.Paciente;
import com.nutricionalcoach.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    @Transactional
    public PacienteDTO crearPaciente(PacienteDTO pacienteDTO) {
        if (pacienteDTO == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (pacienteDTO.getNombre() == null || pacienteDTO.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (pacienteDTO.getEmail() == null || pacienteDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        Paciente paciente = new Paciente(
                null,
                pacienteDTO.getNombre(),
                pacienteDTO.getApellidos(),
                pacienteDTO.getEmail(),
                pacienteDTO.getTelefono(),
                pacienteDTO.getObjetivo(),
                pacienteDTO.getFechaAlta());
        Paciente guardado = pacienteRepository.save(paciente);
        return toDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDTO> listarPacientesActivos(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 20;
        }
        List<Paciente> activos = pacienteRepository.findByActivoTrue();
        int from = (page - 1) * size;
        int to = Math.min(from + size, activos.size());
        if (from > activos.size()) {
            from = activos.size();
        }
        return activos.subList(from, to).stream()
                .map(this::toDto)
                .toList();
    }

    private PacienteDTO toDto(Paciente paciente) {
        return new PacienteDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellidos(),
                paciente.getEmail(),
                paciente.getTelefono(),
                paciente.getObjetivo(),
                paciente.getFechaAlta());
    }
}
