package com.nutricionalcoach.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio: paciente.
 * Usa soft-delete (activo=false + fechaBaja) en lugar de eliminación física.
 */
public class Paciente {

    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String objetivo;
    private LocalDate fechaAlta;
    private Boolean activo = true;
    private LocalDate fechaBaja;

    public Paciente() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    public Paciente(String id, String nombre, String apellidos, String email,
                    String telefono, String objetivo, LocalDate fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.objetivo = objetivo;
        this.fechaAlta = fechaAlta;
        this.activo = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }

    public void softDelete() {
        this.activo = false;
        this.fechaBaja = LocalDate.now();
    }
}
