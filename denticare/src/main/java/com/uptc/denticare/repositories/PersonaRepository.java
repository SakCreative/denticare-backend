package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio JPA para acceder a los registros de la tabla "persona".
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // Busca una persona por su número de documento.
    Optional<Persona> findByDocumento(String documento);

    // Verifica si ya existe una persona registrada con ese documento.
    boolean existsByDocumento(String documento);
}