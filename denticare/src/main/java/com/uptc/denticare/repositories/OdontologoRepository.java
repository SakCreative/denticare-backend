package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {

    boolean existsByIdUsuario(Long idUsuario);

    boolean existsByIdUsuarioAndIdOdontologoNot(Long idUsuario, Long idOdontologo);

    boolean existsByRegistroMedicoIgnoreCase(String registroMedico);

    boolean existsByRegistroMedicoIgnoreCaseAndIdOdontologoNot(String registroMedico, Long idOdontologo);

    Optional<Odontologo> findByIdUsuario(Long idUsuario);

    Optional<Odontologo> findByRegistroMedicoIgnoreCase(String registroMedico);

    List<Odontologo> findByEspecialidadIgnoreCase(String especialidad);
}