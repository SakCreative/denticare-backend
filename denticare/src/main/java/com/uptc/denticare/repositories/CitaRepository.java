package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    // 1. Básico: Todas las citas de un doctor (Útil para estadísticas globales)
    List<Cita> findByOdontologo_IdOdontologo(Long idOdontologo);

    // NFR + FUNCIONAL: Citas de todos los doctores de una especialidad en un rango de fechas
    List<Cita> findByOdontologo_EspecialidadAndFechaHoraInicioBetween(
            String especialidad, LocalDateTime inicio, LocalDateTime fin);

    // 2. RENDIMIENTO (NFR): Citas de un doctor en un mes/semana específico (Para el Calendario)
    List<Cita> findByOdontologo_IdOdontologoAndFechaHoraInicioBetween(
            Long idOdontologo, LocalDateTime inicio, LocalDateTime fin);

    // 3. FUNCIONAL: Historial de citas de un paciente específico
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    // 4. FUNCIONAL: Filtrar la agenda por estado (ej: Ver solo las "PROGRAMADA")
    List<Cita> findByOdontologo_IdOdontologoAndEstado(Long idOdontologo, String estado);
}