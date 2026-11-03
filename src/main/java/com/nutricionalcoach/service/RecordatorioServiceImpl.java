package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RecordatorioDTO;
import com.nutricionalcoach.dto.RecordatorioFeedbackDTO;
import com.nutricionalcoach.model.Recordatorio;
import com.nutricionalcoach.repository.RecordatorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de recordatorios.
 * Lista los recordatorios pendientes (estado PENDIENTE) y actualiza el
 * feedback del paciente de un recordatorio existente.
 */
@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;

    public RecordatorioServiceImpl(RecordatorioRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordatorioDTO> listarRecordatoriosPendientes() {
        return recordatorioRepository.findByEstado("PENDIENTE").stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<RecordatorioDTO> actualizarFeedback(String id, RecordatorioFeedbackDTO feedback) {
        Optional<Recordatorio> recordatorio = recordatorioRepository.findById(id);
        if (recordatorio.isEmpty()) {
            return Optional.empty();
        }
        Recordatorio r = recordatorio.get();
        r.setFeedbackPaciente(feedback.getFeedbackPaciente());
        return Optional.of(toDto(recordatorioRepository.save(r)));
    }

    private RecordatorioDTO toDto(Recordatorio recordatorio) {
        return new RecordatorioDTO(
                recordatorio.getId(),
                recordatorio.getPacienteId(),
                recordatorio.getFechaProgramada(),
                recordatorio.getTipo(),
                recordatorio.getEstado(),
                recordatorio.getFeedbackPaciente());
    }
}
