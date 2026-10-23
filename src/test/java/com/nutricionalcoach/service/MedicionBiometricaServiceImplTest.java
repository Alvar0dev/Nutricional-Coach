package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;
import com.nutricionalcoach.model.MedicionBiometrica;
import com.nutricionalcoach.repository.MedicionBiometricaRepository;
import com.nutricionalcoach.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicionBiometricaServiceImplTest {

    @Mock
    private MedicionBiometricaRepository medicionRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private MedicionBiometricaServiceImpl servicio;

    @Test
    void registrarMedicionValida_guardaYRetornaDto() {
        MedicionBiometricaDTO dto = new MedicionBiometricaDTO(
                null, "paciente-1", LocalDate.of(2024, 6, 1),
                75.0, 18.5, 62.0, 85.0);
        when(pacienteRepository.existsById("paciente-1")).thenReturn(true);

        MedicionBiometrica guardada = new MedicionBiometrica();
        guardada.setId("med-1");
        guardada.setPacienteId("paciente-1");
        guardada.setFecha(LocalDate.of(2024, 6, 1));
        guardada.setPesoKg(75.0);
        guardada.setGrasaPct(18.5);
        guardada.setMasaMuscularKg(62.0);
        guardada.setCinturaCm(85.0);
        when(medicionRepository.save(any(MedicionBiometrica.class))).thenReturn(guardada);

        MedicionBiometricaDTO resultado = servicio.registrarMedicion("paciente-1", dto);

        assertThat(resultado.getId()).isEqualTo("med-1");
        assertThat(resultado.getPacienteId()).isEqualTo("paciente-1");
        assertThat(resultado.getPesoKg()).isEqualTo(75.0);
        assertThat(resultado.getGrasaPct()).isEqualTo(18.5);
        verify(medicionRepository, times(1)).save(any(MedicionBiometrica.class));
    }

    @Test
    void registrarMedicionPacienteInexistente_lanzaExcepcion() {
        MedicionBiometricaDTO dto = new MedicionBiometricaDTO(
                null, "paciente-999", LocalDate.of(2024, 6, 1),
                75.0, 18.5, 62.0, 85.0);
        when(pacienteRepository.existsById("paciente-999")).thenReturn(false);

        assertThatThrownBy(() -> servicio.registrarMedicion("paciente-999", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Paciente no encontrado");
        verify(medicionRepository, never()).save(any());
    }

    @Test
    void registrarMedicionPesoNegativo_lanzaExcepcion() {
        MedicionBiometricaDTO dto = new MedicionBiometricaDTO(
                null, "paciente-1", LocalDate.of(2024, 6, 1),
                -5.0, 18.5, 62.0, 85.0);

        assertThatThrownBy(() -> servicio.registrarMedicion("paciente-1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pesoKg no puede ser negativo");
    }

    @Test
    void registrarMedicionGrasaNegativa_lanzaExcepcion() {
        MedicionBiometricaDTO dto = new MedicionBiometricaDTO(
                null, "paciente-1", LocalDate.of(2024, 6, 1),
                75.0, -2.0, 62.0, 85.0);

        assertThatThrownBy(() -> servicio.registrarMedicion("paciente-1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("grasaPct no puede ser negativo");
    }
}
