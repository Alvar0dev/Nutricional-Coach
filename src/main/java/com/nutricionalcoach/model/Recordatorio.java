package com.nutricionalcoach.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio: recordatorio de alerta para un paciente.
 * Representa una cita de control o revisión mensual programada, con su estado
 * de pendiente o completado y un campo opcional de feedback del paciente.
 */
@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    private String id;
    private String pacienteId;
    private LocalDate fechaProgramada;
    private String tipo; // CONTROL | REVISION_MENSUAL
    private String estado; // PENDIENTE | COMPLETADO
    private String feedbackPaciente;

    public Recordatorio() {
        this.id = UUID.randomUUID().toString();
        this.estado = "PENDIENTE";
    }

    public Recordatorio(String id, String pacienteId, LocalDate fechaProgramada,
                        String tipo, String estado, String feedbackPaciente) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.fechaProgramada = fechaProgramada;
        this.tipo = tipo;
        this.estado = estado;
        this.feedbackPaciente = feedbackPaciente;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFeedbackPaciente() { return feedbackPaciente; }
    public void setFeedbackPaciente(String feedbackPaciente) { this.feedbackPaciente = feedbackPaciente; }
}
