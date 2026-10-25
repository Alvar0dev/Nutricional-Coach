package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.PacienteDTO;
import com.nutricionalcoach.model.Paciente;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl servicio;

    @Test
    void softDeletePacienteExistente_marcaActivoFalseYFechaBaja() {
        Paciente paciente = new Paciente("1", "Juan", "Pérez", "juan@test.com",
                "123456789", "perder peso", LocalDate.of(2024, 1, 1));
        when(pacienteRepository.findById("1")).thenReturn(Optional.of(paciente));

        boolean resultado = servicio.softDeletePaciente("1");

        assertThat(resultado).isTrue();
        assertThat(paciente.getActivo()).isFalse();
        assertThat(paciente.getFechaBaja()).isEqualTo(LocalDate.now());
        verify(pacienteRepository, times(1)).save(paciente);
    }

    @Test
    void softDeletePacienteInexistente_retornaFalse() {
        when(pacienteRepository.findById("no-existe")).thenReturn(Optional.empty());

        boolean resultado = servicio.softDeletePaciente("no-existe");

        assertThat(resultado).isFalse();
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void listarPacientesActivos_devuelveSoloActivosMapeadosADto() {
        Paciente activo = new Paciente("1", "Juan", "Pérez", "juan@test.com",
                "123456789", "perder peso", LocalDate.of(2024, 1, 1));
        Paciente inactivo = new Paciente("2", "Ana", "García", "ana@test.com",
                "987654321", "ganar masa", LocalDate.of(2024, 2, 1));
        inactivo.softDelete();

        when(pacienteRepository.findByActivoTrue()).thenReturn(List.of(activo));

        List<PacienteDTO> resultado = servicio.listarPacientesActivos(1, 20);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo("1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
        verify(pacienteRepository, times(1)).findByActivoTrue();
    }
}
