package com.nutricionalcoach.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio: medición biométrica.
 * Inmutable una vez creada (no hay PUT ni DELETE en la especificación).
 */
public class MedicionBiometrica {

    private String id;
    private String pacienteId;
    private LocalDate fecha;
    private Double pesoKg;
    private Double grasaPct;
    private Double masaMuscularKg;
    private Double cinturaCm;

    public MedicionBiometrica() {
        this.id = UUID.randomUUID().toString();
    }

    public MedicionBiometrica(String id, String pacienteId, LocalDate fecha,
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
