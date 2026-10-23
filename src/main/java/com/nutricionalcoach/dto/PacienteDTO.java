package com.nutricionalcoach.dto;

import java.time.LocalDate;

/**
 * DTO de paciente, alineado con el schema PacienteDTO de openapi.yaml.
 */
public class PacienteDTO {

    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String objetivo;
    private LocalDate fechaAlta;

    public PacienteDTO() {}

    public PacienteDTO(String id, String nombre, String apellidos, String email,
                       String telefono, String objetivo, LocalDate fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.objetivo = objetivo;
        this.fechaAlta = fechaAlta;
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
}
