package com.uptc.denticare.services;

import com.uptc.denticare.models.Persona;
import com.uptc.denticare.repositories.PersonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Capa de servicio para la gestión de personas en DentiCare.
 * Valida datos obligatorios y evita duplicados por documento sin realizar
 * eliminaciones físicas para preservar la integridad con otras entidades.
 */
@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Registra una nueva persona validando documento, nombres y apellidos,
     * además de impedir que se repita el mismo documento.
     */
    @Transactional
    public Persona crear(Persona request) {
        if (request == null || request.getDocumento() == null || request.getDocumento().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El campo documento es obligatorio");
        }
        if (request.getNombres() == null || request.getNombres().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El campo nombres es obligatorio");
        }
        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El campo apellidos es obligatorio");
        }

        String documentoLimpio = request.getDocumento().trim();
        if (personaRepository.existsByDocumento(documentoLimpio)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una persona registrada con el documento: " + documentoLimpio);
        }

        Persona nuevaPersona = new Persona();
        nuevaPersona.setDocumento(documentoLimpio);
        nuevaPersona.setNombres(request.getNombres().trim());
        nuevaPersona.setApellidos(request.getApellidos().trim());
        nuevaPersona.setTelefono(request.getTelefono() != null ? request.getTelefono().trim() : null);
        nuevaPersona.setCorreo(request.getCorreo() != null ? request.getCorreo().trim() : null);

        return personaRepository.save(nuevaPersona);
    }

    /**
     * Lista personas de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<Persona> listar(Pageable pageable) {
        return personaRepository.findAll(pageable);
    }

    /**
     * Consulta una persona por su id_persona.
     */
    @Transactional(readOnly = true)
    public Persona buscarPorId(Long idPersona) {
        return personaRepository.findById(idPersona)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró la persona con id: " + idPersona));
    }

    /**
     * Consulta una persona por su número de documento.
     */
    @Transactional(readOnly = true)
    public Persona buscarPorDocumento(String documento) {
        return personaRepository.findByDocumento(documento.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró la persona con documento: " + documento));
    }

    /**
     * Actualiza los datos editables de una persona existente.
     */
    @Transactional
    public Persona actualizar(Long idPersona, Persona request) {
        Persona existente = personaRepository.findById(idPersona)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró la persona con id: " + idPersona));

        if (request != null) {
            if (request.getDocumento() != null && !request.getDocumento().isBlank()) {
                String nuevoDoc = request.getDocumento().trim();
                if (!nuevoDoc.equals(existente.getDocumento()) && personaRepository.existsByDocumento(nuevoDoc)) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "El documento ya está en uso por otra persona: " + nuevoDoc);
                }
                existente.setDocumento(nuevoDoc);
            }
            if (request.getNombres() != null && !request.getNombres().isBlank()) {
                existente.setNombres(request.getNombres().trim());
            }
            if (request.getApellidos() != null && !request.getApellidos().isBlank()) {
                existente.setApellidos(request.getApellidos().trim());
            }
            if (request.getTelefono() != null) {
                existente.setTelefono(request.getTelefono().trim());
            }
            if (request.getCorreo() != null) {
                existente.setCorreo(request.getCorreo().trim());
            }
        }

        return personaRepository.save(existente);
    }
}