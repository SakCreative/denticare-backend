package com.uptc.denticare.services;

import com.uptc.denticare.models.Odontologo;
import com.uptc.denticare.repositories.OdontologoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OdontologoService {

    private final OdontologoRepository odontologoRepository;

    public OdontologoService(OdontologoRepository odontologoRepository) {
        this.odontologoRepository = odontologoRepository;
    }

    @Transactional
    public Odontologo crear(Odontologo odontologo) {
        if (odontologo.getIdUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idUsuario es obligatorio.");
        }
        if (odontologo.getRegistroMedico() == null || odontologo.getRegistroMedico().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El registro médico es obligatorio.");
        }
        if (odontologo.getEspecialidad() == null || odontologo.getEspecialidad().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad es obligatoria.");
        }

        String registroLimpio = odontologo.getRegistroMedico().trim();
        String especialidadLimpia = odontologo.getEspecialidad().trim();

        if (odontologoRepository.existsByIdUsuario(odontologo.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un odontólogo asociado al idUsuario: " + odontologo.getIdUsuario());
        }
        if (odontologoRepository.existsByRegistroMedicoIgnoreCase(registroLimpio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un odontólogo con el registro médico: " + registroLimpio);
        }

        odontologo.setIdOdontologo(null);
        odontologo.setRegistroMedico(registroLimpio);
        odontologo.setEspecialidad(especialidadLimpia);

        return odontologoRepository.save(odontologo);
    }

    @Transactional(readOnly = true)
    public Page<Odontologo> listarPaginado(Pageable pageable) {
        return odontologoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Odontologo obtenerPorId(Long idOdontologo) {
        return odontologoRepository.findById(idOdontologo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Odontólogo no encontrado con ID: " + idOdontologo));
    }

    @Transactional(readOnly = true)
    public List<Odontologo> buscarPorEspecialidad(String especialidad) {
        return odontologoRepository.findByEspecialidadIgnoreCase(especialidad.trim());
    }

    @Transactional
    public Odontologo actualizar(Long idOdontologo, Odontologo datosActualizados) {
        Odontologo existente = obtenerPorId(idOdontologo);

        if (datosActualizados.getIdUsuario() != null) {
            if (odontologoRepository.existsByIdUsuarioAndIdOdontologoNot(datosActualizados.getIdUsuario(), idOdontologo)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe otro odontólogo con el idUsuario: " + datosActualizados.getIdUsuario());
            }
            existente.setIdUsuario(datosActualizados.getIdUsuario());
        }

        if (datosActualizados.getRegistroMedico() != null) {
            String registroLimpio = datosActualizados.getRegistroMedico().trim();
            if (registroLimpio.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El registro médico no puede quedar vacío.");
            }
            if (odontologoRepository.existsByRegistroMedicoIgnoreCaseAndIdOdontologoNot(registroLimpio, idOdontologo)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe otro odontólogo con el registro médico: " + registroLimpio);
            }
            existente.setRegistroMedico(registroLimpio);
        }

        if (datosActualizados.getEspecialidad() != null) {
            String especialidadLimpia = datosActualizados.getEspecialidad().trim();
            if (especialidadLimpia.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La especialidad no puede quedar vacía.");
            }
            existente.setEspecialidad(especialidadLimpia);
        }

        return odontologoRepository.save(existente);
    }
}