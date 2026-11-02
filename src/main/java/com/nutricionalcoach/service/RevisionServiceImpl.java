package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RevisionDTO;
import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.RevisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de revisiones mensuales.
 * Calcula deltas entre mediciones anteriores y actuales y registra ajustes
 * de dieta y entreno.
 */
@Service
public class RevisionServiceImpl implements RevisionService {

    private final RevisionRepository revisionRepository;

    public RevisionServiceImpl(RevisionRepository revisionRepository) {
        this.revisionRepository = revisionRepository;
    }

    @Override
    @Transactional
    public RevisionDTO crearRevision(RevisionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("La revisión no puede ser nula");
        }
        if (dto.getPacienteId() == null || dto.getPacienteId().isBlank()) {
            throw new IllegalArgumentException("El paciente es obligatorio");
        }

        Revision revision = new Revision();
        revision.setPacienteId(dto.getPacienteId());
        revision.setMedicionAnteriorId(dto.getMedicionAnteriorId());
        revision.setMedicionActualId(dto.getMedicionActualId());
        revision.setDeltaPeso(dto.getDeltaPeso());
        revision.setDeltaGrasa(dto.getDeltaGrasa());
        revision.setDeltaMusculo(dto.getDeltaMusculo());
        revision.setComentariosAjuste(dto.getComentariosAjuste());
        revision.setAjustesDieta(dto.getAjustesDieta());
        revision.setAjustesEntreno(dto.getAjustesEntreno());
        revision.setPdfUrl(dto.getPdfUrl());
        revision.setFechaRevision(dto.getFechaRevision() != null ? dto.getFechaRevision() : LocalDate.now());

        Revision guardada = revisionRepository.save(revision);
        return toDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionDTO> listarRevisiones() {
        return revisionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RevisionDTO obtenerRevisionPorId(String id) {
        return revisionRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    private RevisionDTO toDto(Revision revision) {
        return new RevisionDTO(
                revision.getId(),
                revision.getPacienteId(),
                revision.getMedicionAnteriorId(),
                revision.getMedicionActualId(),
                revision.getDeltaPeso(),
                revision.getDeltaGrasa(),
                revision.getDeltaMusculo(),
                revision.getComentariosAjuste(),
                revision.getAjustesDieta(),
                revision.getAjustesEntreno(),
                revision.getPdfUrl(),
                revision.getFechaRevision());
    }
}
