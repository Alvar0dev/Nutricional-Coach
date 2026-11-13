package com.nutricionalcoach.dto;

/**
 * DTO de actualización de feedback de recordatorio, alineado con el schema
 * RecordatorioFeedbackDTO de openapi.yaml.
 */
public class RecordatorioFeedbackDTO {

    private String feedbackPaciente;

    public RecordatorioFeedbackDTO() {}

    public RecordatorioFeedbackDTO(String feedbackPaciente) {
        this.feedbackPaciente = feedbackPaciente;
    }

    public String getFeedbackPaciente() { return feedbackPaciente; }
    public void setFeedbackPaciente(String feedbackPaciente) { this.feedbackPaciente = feedbackPaciente; }
}
