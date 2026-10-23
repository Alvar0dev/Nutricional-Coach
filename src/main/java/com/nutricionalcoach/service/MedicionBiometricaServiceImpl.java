package com.nutricionalcoach.service;

import com.nutricionalcoach.dto.MedicionBiometricaDTO;
import com.nutricionalcoach.model.MedicionBiometrica;
import com.nutricionalcoach.repository.MedicionBiometricaRepository;
import com.nutricionalcoach.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementación del servicio de mediciones biométricas.
 * Valida pesoKg y grasaPct no negativos antes de persistir.
 */
@Service
public class MedicionBiometricaServiceImpl implements MedicionBiometricaService {

    private final MedicionBiometricaRepository medicionRepository;
    private final PacienteRepository pacienteRepository;

    public MedicionBiometricaServiceImpl(MedicionBiometricaRepository medicionRepository,
                                         PacienteRepository pacienteRepository) {
        this.medicionRepository = medicionRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional
    public MedicionBiometricaDTO registrarMedicion(String pacienteId, MedicionBiometricaDTO dto) {
        validarMedicion(dto);

        if (!pacienteRepository.existsById(pacienteId)) {
            throw new IllegalArgumentException("Paciente no encontrado: " + pacienteId);
        }

        MedicionBiometrica medicion = new MedicionBiometrica();
        medicion.setPacienteId(pacienteId);
        medicion.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        medicion.setPesoKg(dto.getPesoKg());
        medicion.setGrasaPct(dto.getGrasaPct());
        medicion.setMasaMuscularKg(dto.getMasaMuscularKg());
        medicion.setCinturaCm(dto.getCinturaCm());

        MedicionBiometrica guardada = medicionRepository.save(medicion);

        return new MedicionBiometricaDTO(
                guardada.getId(),
                guardada.getPacienteId(),
                guardada.getFecha(),
                guardada.getPesoKg(),
                guardada.getGrasaPct(),
                guardada.getMasaMuscularKg(),
                guardada.getCinturaCm()
        );
    }

    private void validarMedicion(MedicionBiometricaDTO dto) {
        if (dto.getPesoKg() != null && dto.getPesoKg() < 0) {
            throw new IllegalArgumentException("pesoKg no puede ser negativo");
        }
        if (dto.getGrasaPct() != null && dto.getGrasaPct() < 0) {
            throw new IllegalArgumentException("grasaPct no puede ser negativo");
        }
    }
}
