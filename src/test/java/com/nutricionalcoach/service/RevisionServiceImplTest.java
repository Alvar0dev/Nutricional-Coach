package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.RevisionDTO;
import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.RevisionRepository;
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
class RevisionServiceImplTest {

    @Mock
    private RevisionRepository revisionRepository;

    @InjectMocks
    private RevisionServiceImpl servicio;

    @Test
    void crearRevisionValida_guardaYRetornaDto() {
        RevisionDTO dto = new RevisionDTO(
                null, "paciente-1", "med-1", "med-2",
                -1.5, -0.5, 0.5,
                "mejora general", "más proteínas", "más cardio",
                null, LocalDate.of(2024, 7, 1));
        when(revisionRepository.save(any(Revision.class))).thenAnswer(invocation -> {
            Revision r = invocation.getArgument(0);
            r.setId("rev-1");
            return r;
        });

        RevisionDTO resultado = servicio.crearRevision(dto);

        assertThat(resultado.getId()).isEqualTo("rev-1");
        assertThat(resultado.getPacienteId()).isEqualTo("paciente-1");
        assertThat(resultado.getDeltaPeso()).isEqualTo(-1.5);
        assertThat(resultado.getFechaRevision()).isEqualTo(LocalDate.of(2024, 7, 1));
        verify(revisionRepository, times(1)).save(any(Revision.class));
    }

    @Test
    void crearRevisionSinPaciente_lanzaExcepcion() {
        RevisionDTO dto = new RevisionDTO(
                null, null, "med-1", "med-2",
                -1.5, -0.5, 0.5, null, null, null, null, LocalDate.now());

        assertThatThrownBy(() -> servicio.crearRevision(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El paciente es obligatorio");
        verify(revisionRepository, never()).save(any());
    }

    @Test
    void crearRevisionNula_lanzaExcepcion() {
        assertThatThrownBy(() -> servicio.crearRevision(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La revisión no puede ser nula");
    }

    @Test
    void listarRevisiones_devuelveLista() {
        Revision r1 = new Revision();
        r1.setId("rev-1"); r1.setPacienteId("paciente-1");
        r1.setFechaRevision(LocalDate.of(2024, 7, 1));
        Revision r2 = new Revision();
        r2.setId("rev-2"); r2.setPacienteId("paciente-1");
        r2.setFechaRevision(LocalDate.of(2024, 8, 1));
        when(revisionRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RevisionDTO> resultado = servicio.listarRevisiones();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo("rev-1");
        assertThat(resultado.get(1).getId()).isEqualTo("rev-2");
        verify(revisionRepository, times(1)).findAll();
    }

    @Test
    void obtenerRevisionExistente_devuelveDto() {
        Revision r = new Revision();
        r.setId("rev-1"); r.setPacienteId("paciente-1");
        r.setFechaRevision(LocalDate.of(2024, 7, 1));
        when(revisionRepository.findById("rev-1")).thenReturn(Optional.of(r));

        RevisionDTO resultado = servicio.obtenerRevisionPorId("rev-1");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("rev-1");
        assertThat(resultado.getPacienteId()).isEqualTo("paciente-1");
    }

    @Test
    void obtenerRevisionInexistente_devuelveNulo() {
        when(revisionRepository.findById("rev-999")).thenReturn(Optional.empty());

        RevisionDTO resultado = servicio.obtenerRevisionPorId("rev-999");

        assertThat(resultado).isNull();
    }

    @Test
    void generarPdfRevisionExistente_devuelveBytes() {
        Revision r = new Revision();
        r.setId("rev-1"); r.setPacienteId("paciente-1");
        r.setFechaRevision(LocalDate.of(2024, 7, 1));
        r.setDeltaPeso(-1.5); r.setDeltaGrasa(-0.5); r.setDeltaMusculo(0.5);
        r.setAjustesDieta("más proteínas"); r.setAjustesEntreno("más cardio");
        r.setComentariosAjuste("mejora general");
        when(revisionRepository.findById("rev-1")).thenReturn(Optional.of(r));

        byte[] pdf = servicio.generarPdfRevision("rev-1");

        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);
        verify(revisionRepository, times(1)).findById("rev-1");
    }

    @Test
    void generarPdfRevisionInexistente_devuelveNulo() {
        when(revisionRepository.findById("rev-999")).thenReturn(Optional.empty());
        byte[] pdf = servicio.generarPdfRevision("rev-999");
        assertThat(pdf).isNull();
    }
}
