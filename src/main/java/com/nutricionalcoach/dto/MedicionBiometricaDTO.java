package com.nutricionalcoach.dto;

import java.time.LocalDate;

/**
 * DTO de medición biométrica, alineado con el schema MedicionBiometricaDTO de openapi.yaml.
 * pesoKg y grasaPct deben ser >= 0 (validación de la especificación).
 */
public class MedicionBiometricaDTO {

    private String id;
    private String pacienteId;
    private LocalDate fecha;
    private Double pesoKg;
    private Double grasaPct;
    private Double masaMuscularKg;
    private Double cinturaCm;

    public MedicionBiometricaDTO() {}

    public MedicionBiometricaDTO(String id, String pacienteId, LocalDate fecha,
                                 Double pesoKg, Double grasaPct,
                                 Double masaMuscularKg, Double cinturaCm) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.fecha = fecha;
        this.pesoKg = pesoKg;
        this.grasaPct = grasaPct;
        this.masaMuscularKg = masaMuscularKg;
        this.cinturaCm = cinturaCm;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getPesoKg() { return pesoKg; }
    public void setPesoKg(Double pesoKg) { this.pesoKg = pesoKg; }

    public Double getGrasaPct() { return grasaPct; }
    public void setGrasaPct(Double grasaPct) { this.grasaPct = grasaPct; }

    public Double getMasaMuscularKg() { return masaMuscularKg; }
    public void setMasaMuscularKg(Double masaMuscularKg) { this.masaMuscularKg = masaMuscularKg; }

    public Double getCinturaCm() { return cinturaCm; }
    public void setCinturaCm(Double cinturaCm) { this.cinturaCm = cinturaCm; }
}
