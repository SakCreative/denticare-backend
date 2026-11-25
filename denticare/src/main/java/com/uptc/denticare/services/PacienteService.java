package com.uptc.denticare.services;

import com.uptc.denticare.models.Paciente;
import com.uptc.denticare.repositories.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Capa de servicio para la gestión de pacientes en DentiCare.
 * Centraliza validaciones de integridad y operaciones de consulta/actualización
 * sin realizar eliminaciones físicas para proteger citas, historias clínicas y facturas.
 */
@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Registra un nuevo paciente verificando los campos obligatorios del modelo.
     * La restricción UNIQUE de id_persona y la FK hacia persona se validan en base de datos.
     */
    @Transactional
    public Paciente crear(Paciente request) {
        if (request == null || request.getIdPersona() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El campo idPersona es obligatorio para registrar un paciente");
        }
        if (request.getFechaNacimiento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El campo fechaNacimiento es obligatorio para registrar un paciente");
        }

        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setIdPersona(request.getIdPersona());
        nuevoPaciente.setFechaNacimiento(request.getFechaNacimiento());

        return pacienteRepository.save(nuevoPaciente);
    }

    /**
     * Lista pacientes de forma paginada para tablas, búsquedas y selectores del sistema.
     */
    @Transactional(readOnly = true)
    public Page<Paciente> listar(Pageable pageable) {
        return pacienteRepository.findAll(pageable);
    }

    /**
     * Consulta un paciente por ID cargando su historia clínica asociada mediante LEFT JOIN FETCH.
     */
    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long idPaciente) {
        return pacienteRepository.findByIdConHistoria(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró el paciente con id: " + idPaciente));
    }

    /**
     * Actualiza los datos editables del paciente sin romper sus relaciones clínicas ni de facturación.
     */
    @Transactional
    public Paciente actualizar(Long idPaciente, Paciente request) {
        Paciente pacienteExistente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró el paciente con id: " + idPaciente));

        if (request != null) {
            if (request.getIdPersona() != null) {
                pacienteExistente.setIdPersona(request.getIdPersona());
            }
            if (request.getFechaNacimiento() != null) {
                pacienteExistente.setFechaNacimiento(request.getFechaNacimiento());
            }
        }

        return pacienteRepository.save(pacienteExistente);
    }
}