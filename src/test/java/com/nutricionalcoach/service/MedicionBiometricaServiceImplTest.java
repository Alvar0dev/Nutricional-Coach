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
import java.util.List;
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

    @Test
    void listarMedicionesPacienteExistente_devuelveLista() {
        when(pacienteRepository.existsById("paciente-1")).thenReturn(true);

        MedicionBiometrica m1 = new MedicionBiometrica();
        m1.setId("med-1"); m1.setPacienteId("paciente-1");
        m1.setFecha(LocalDate.of(2024, 6, 1));
        m1.setPesoKg(75.0); m1.setGrasaPct(18.5); m1.setMasaMuscularKg(62.0); m1.setCinturaCm(85.0);
        MedicionBiometrica m2 = new MedicionBiometrica();
        m2.setId("med-2"); m2.setPacienteId("paciente-1");
        m2.setFecha(LocalDate.of(2024, 7, 1));
        m2.setPesoKg(74.0); m2.setGrasaPct(18.0); m2.setMasaMuscularKg(62.5); m2.setCinturaCm(84.0);
        when(medicionRepository.findByPacienteId("paciente-1")).thenReturn(List.of(m1, m2));

        List<MedicionBiometricaDTO> resultado = servicio.listarMediciones("paciente-1");

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo("med-1");
        assertThat(resultado.get(0).getPesoKg()).isEqualTo(75.0);
        assertThat(resultado.get(1).getId()).isEqualTo("med-2");
        assertThat(resultado.get(1).getPesoKg()).isEqualTo(74.0);
        verify(medicionRepository, times(1)).findByPacienteId("paciente-1");
    }

    @Test
    void listarMedicionesPacienteInexistente_lanzaExcepcion() {
        when(pacienteRepository.existsById("paciente-999")).thenReturn(false);

        assertThatThrownBy(() -> servicio.listarMediciones("paciente-999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Paciente no encontrado");
        verify(medicionRepository, never()).findByPacienteId(anyString());
    }
}
