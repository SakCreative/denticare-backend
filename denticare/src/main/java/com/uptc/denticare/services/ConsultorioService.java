package com.uptc.denticare.services;

import com.uptc.denticare.models.Consultorio;
import com.uptc.denticare.repositories.ConsultorioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConsultorioService {

    private final ConsultorioRepository consultorioRepository;

    public ConsultorioService(ConsultorioRepository consultorioRepository) {
        this.consultorioRepository = consultorioRepository;
    }

    @Transactional
    public Consultorio crear(Consultorio consultorio) {
        if (consultorio.getNombreSala() == null || consultorio.getNombreSala().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la sala es obligatorio.");
        }

        String nombreLimpio = consultorio.getNombreSala().trim();

        if (consultorioRepository.existsByNombreSalaIgnoreCase(nombreLimpio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un consultorio registrado con el nombre: " + nombreLimpio);
        }

        consultorio.setIdConsultorio(null); // Asegura inserción nueva en PostgreSQL
        consultorio.setNombreSala(nombreLimpio);

        // Si no envían el estado en el JSON, por defecto el consultorio nace activo (true)
        if (consultorio.getEstado() == null) {
            consultorio.setEstado(true);
        }

        return consultorioRepository.save(consultorio);
    }

    @Transactional(readOnly = true)
    public Page<Consultorio> listarPaginado(Pageable pageable) {
        return consultorioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Consultorio> listarPorEstado(Boolean estado) {
        return consultorioRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Consultorio obtenerPorId(Long idConsultorio) {
        return consultorioRepository.findById(idConsultorio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Consultorio no encontrado con ID: " + idConsultorio));
    }

    @Transactional
    public Consultorio actualizar(Long idConsultorio, Consultorio datosActualizados) {
        Consultorio existente = obtenerPorId(idConsultorio);

        if (datosActualizados.getNombreSala() != null) {
            String nombreLimpio = datosActualizados.getNombreSala().trim();
            if (nombreLimpio.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El nombre de la sala no puede quedar vacío.");
            }
            if (consultorioRepository.existsByNombreSalaIgnoreCaseAndIdConsultorioNot(nombreLimpio, idConsultorio)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe otro consultorio con el nombre: " + nombreLimpio);
            }
            existente.setNombreSala(nombreLimpio);
        }

        // Permite activar o desactivar (borrado lógico) el consultorio sin perder historial de citas
        if (datosActualizados.getEstado() != null) {
            existente.setEstado(datosActualizados.getEstado());
        }

        return consultorioRepository.save(existente);
    }
}