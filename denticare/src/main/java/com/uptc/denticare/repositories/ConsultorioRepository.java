package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {

    // Evita registrar dos consultorios con exactamente el mismo nombre de sala (ignorando mayúsculas/minúsculas)
    boolean existsByNombreSalaIgnoreCase(String nombreSala);

    // Útil al actualizar: verifica si otro consultorio diferente ya usa ese nombre de sala
    boolean existsByNombreSalaIgnoreCaseAndIdConsultorioNot(String nombreSala, Long idConsultorio);

    Optional<Consultorio> findByNombreSalaIgnoreCase(String nombreSala);

    // Permite listar solo los consultorios activos (estado = true) o inactivos (estado = false)
    List<Consultorio> findByEstado(Boolean estado);
}