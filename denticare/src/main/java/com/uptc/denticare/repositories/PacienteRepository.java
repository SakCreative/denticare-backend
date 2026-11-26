package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByIdPersona(Long idPersona);

    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.historiaClinica WHERE p.idPaciente = :id")
    Optional<Paciente> findByIdConHistoria(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Paciente p WHERE p.idPaciente = :id")
    boolean existePacientePorId(@Param("id") Long id);
}