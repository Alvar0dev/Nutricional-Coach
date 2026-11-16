package com.nutricionalcoach.config;

import com.nutricionalcoach.model.MedicionBiometrica;
import com.nutricionalcoach.model.Paciente;
import com.nutricionalcoach.model.Recordatorio;
import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.MedicionBiometricaRepository;
import com.nutricionalcoach.repository.PacienteRepository;
import com.nutricionalcoach.repository.RecordatorioRepository;
import com.nutricionalcoach.repository.RevisionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds sample domain records when running with the "local" Spring profile.
 * Keeps local demo environments ready for immediate UI exploration in :4200.
 */
@Component
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    private final PacienteRepository pacienteRepository;
    private final MedicionBiometricaRepository medicionBiometricaRepository;
    private final RevisionRepository revisionRepository;
    private final RecordatorioRepository recordatorioRepository;

    public DataInitializer(PacienteRepository pacienteRepository,
                           MedicionBiometricaRepository medicionBiometricaRepository,
                           RevisionRepository revisionRepository,
                           RecordatorioRepository recordatorioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicionBiometricaRepository = medicionBiometricaRepository;
        this.revisionRepository = revisionRepository;
        this.recordatorioRepository = recordatorioRepository;
    }

    @Override
    public void run(String... args) {
        if (pacienteRepository.count() > 0) {
            return;
        }

        Paciente demoPaciente = new Paciente(
                "pac-demo-1",
                "Carlos",
                "Méndez",
                "carlos.mendez@example.com",
                "611223344",
                "Recomposición corporal y rendimiento",
                LocalDate.of(2026, 9, 15)
        );
        pacienteRepository.save(demoPaciente);

        MedicionBiometrica medicionAnterior = new MedicionBiometrica(
                "med-demo-1",
                "pac-demo-1",
                LocalDate.of(2026, 10, 1),
                78.0,
                19.5,
                58.0,
                82.0
        );
        MedicionBiometrica medicionActual = new MedicionBiometrica(
                "med-demo-2",
                "pac-demo-1",
                LocalDate.of(2026, 11, 1),
                76.2,
                17.8,
                59.1,
                79.5
        );
        medicionBiometricaRepository.save(medicionAnterior);
        medicionBiometricaRepository.save(medicionActual);

        Revision demoRevision = new Revision(
                "rev-demo-1",
                "pac-demo-1",
                "med-demo-1",
                "med-demo-2",
                -1.8,
                -1.7,
                1.1,
                "Buena adherencia y mejora en composición corporal",
                "Ajustar a 2350 kcal con 160g de proteína diaria",
                "4 sesiones de fuerza + 2 sesiones de cardio zona 2",
                "/api/v1/revisiones/rev-demo-1/pdf",
                LocalDate.of(2026, 11, 2)
        );
        revisionRepository.save(demoRevision);

        Recordatorio demoRecordatorio = new Recordatorio(
                "rec-demo-1",
                "pac-demo-1",
                LocalDate.of(2026, 12, 1),
                "REVISION_MENSUAL",
                "PENDIENTE",
                null
        );
        recordatorioRepository.save(demoRecordatorio);
    }
}
