package com.uptc.denticare.repositories;

import com.uptc.denticare.models.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    // Verifica si el paciente tiene una historia clínica ACTIVA
    boolean existsByPaciente_IdPacienteAndEstadoHistoriaTrue(Long idPaciente);

    // Busca la historia clínica del paciente solo si está ACTIVA
    Optional<HistoriaClinica> findByPaciente_IdPacienteAndEstadoHistoriaTrue(Long idPaciente);
}