package com.nutricionalcoach.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio: revisión mensual de un paciente.
 * Calcula deltas entre dos mediciones biométricas y registra ajustes de dieta/entreno.
 */
@Entity
@Table(name = "revisiones")
public class Revision {

    @Id
    private String id;
    private String pacienteId;
    private String medicionAnteriorId;
    private String medicionActualId;
    private Double deltaPeso;
    private Double deltaGrasa;
    private Double deltaMusculo;
    private String comentariosAjuste;
    private String ajustesDieta;
    private String ajustesEntreno;
    private String pdfUrl;
    private LocalDate fechaRevision;

    public Revision() {
        this.id = UUID.randomUUID().toString();
    }

    public Revision(String id, String pacienteId, String medicionAnteriorId,
                    String medicionActualId, Double deltaPeso, Double deltaGrasa,
                    Double deltaMusculo, String comentariosAjuste, String ajustesDieta,
                    String ajustesEntreno, String pdfUrl, LocalDate fechaRevision) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicionAnteriorId = medicionAnteriorId;
        this.medicionActualId = medicionActualId;
        this.deltaPeso = deltaPeso;
        this.deltaGrasa = deltaGrasa;
        this.deltaMusculo = deltaMusculo;
        this.comentariosAjuste = comentariosAjuste;
        this.ajustesDieta = ajustesDieta;
        this.ajustesEntreno = ajustesEntreno;
        this.pdfUrl = pdfUrl;
        this.fechaRevision = fechaRevision;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getMedicionAnteriorId() { return medicionAnteriorId; }
    public void setMedicionAnteriorId(String medicionAnteriorId) { this.medicionAnteriorId = medicionAnteriorId; }

    public String getMedicionActualId() { return medicionActualId; }
    public void setMedicionActualId(String medicionActualId) { this.medicionActualId = medicionActualId; }

    public Double getDeltaPeso() { return deltaPeso; }
    public void setDeltaPeso(Double deltaPeso) { this.deltaPeso = deltaPeso; }

    public Double getDeltaGrasa() { return deltaGrasa; }
    public void setDeltaGrasa(Double deltaGrasa) { this.deltaGrasa = deltaGrasa; }

    public Double getDeltaMusculo() { return deltaMusculo; }
    public void setDeltaMusculo(Double deltaMusculo) { this.deltaMusculo = deltaMusculo; }

    public String getComentariosAjuste() { return comentariosAjuste; }
    public void setComentariosAjuste(String comentariosAjuste) { this.comentariosAjuste = comentariosAjuste; }

    public String getAjustesDieta() { return ajustesDieta; }
    public void setAjustesDieta(String ajustesDieta) { this.ajustesDieta = ajustesDieta; }

    public String getAjustesEntreno() { return ajustesEntreno; }
    public void setAjustesEntreno(String ajustesEntreno) { this.ajustesEntreno = ajustesEntreno; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public LocalDate getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDate fechaRevision) { this.fechaRevision = fechaRevision; }
}
