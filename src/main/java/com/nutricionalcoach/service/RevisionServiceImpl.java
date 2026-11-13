package com.nutricionalcoach.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.nutricionalcoach.dto.RevisionDTO;
import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.RevisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.io.ByteArrayOutputStream;

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

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdfRevision(String id) {
        Revision revision = revisionRepository.findById(id).orElse(null);
        if (revision == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph("Nutricional Coach - Revisión Mensual"));
            document.add(new Paragraph("ID de revisión: " + revision.getId()));
            document.add(new Paragraph("ID de paciente: " + revision.getPacienteId()));
            document.add(new Paragraph("Fecha: " + revision.getFechaRevision()));
            document.add(new Paragraph("Deltas calculados:"));
            document.add(new Paragraph("  Peso: " + revision.getDeltaPeso() + " kg"));
            document.add(new Paragraph("  Grasa: " + revision.getDeltaGrasa() + " %"));
            document.add(new Paragraph("  Músculo: " + revision.getDeltaMusculo() + " kg"));
            document.add(new Paragraph("Ajustes de dieta: " + revision.getAjustesDieta()));
            document.add(new Paragraph("Ajustes de entreno: " + revision.getAjustesEntreno()));
            document.add(new Paragraph("Comentarios: " + revision.getComentariosAjuste()));
            document.close();
        } catch (Exception e) {
            throw new IllegalStateException("Error generando PDF de la revisión", e);
        }
        return baos.toByteArray();
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
