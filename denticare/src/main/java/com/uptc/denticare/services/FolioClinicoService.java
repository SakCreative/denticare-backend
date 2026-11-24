package com.uptc.denticare.services;

import com.uptc.denticare.models.FolioClinico;
import com.uptc.denticare.models.HistoriaClinica;
import com.uptc.denticare.repositories.FolioClinicoRepository;
import com.uptc.denticare.repositories.HistoriaClinicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FolioClinicoService {

    private final FolioClinicoRepository folioRepository;
    private final HistoriaClinicaRepository historiaRepository;

    public FolioClinicoService(FolioClinicoRepository folioRepository,
                               HistoriaClinicaRepository historiaRepository) {
        this.folioRepository = folioRepository;
        this.historiaRepository = historiaRepository;
    }

    // 1. Crear una nueva evolución (Folio)
    public FolioClinico crearFolio(Long idHistoria, FolioClinico nuevoFolio) {
        HistoriaClinica historia = historiaRepository.findById(idHistoria)
                .orElseThrow(() -> new RuntimeException("Error: La historia clínica no existe."));

        // Validar que la historia no esté borrada lógicamente
        if (!historia.getEstadoHistoria()) {
            throw new RuntimeException("Error: No se pueden agregar folios a una historia clínica inactiva.");
        }

        nuevoFolio.setIdFolio(null);
        nuevoFolio.setHistoriaClinica(historia);
        nuevoFolio.setFechaCreacion(LocalDateTime.now()); // Fecha automática del sistema
        nuevoFolio.setEstadoFolio("ACTIVO"); // Estado inicial obligatorio

        return folioRepository.save(nuevoFolio);
    }

    // 2. Listar todas las evoluciones (De la más reciente a la más antigua)
    @Transactional(readOnly = true)
    public List<FolioClinico> obtenerFoliosPorHistoria(Long idHistoria) {
        return folioRepository.findByHistoriaClinica_IdHistoriaOrderByFechaCreacionDesc(idHistoria);
    }

    // 3. REGLA DE PROYECTO: Anular folio exigiendo justificación obligatoria
    public FolioClinico anularFolio(Long idFolio, String justificacion) {
        if (justificacion == null || justificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Error Crítico: Es obligatorio proporcionar una justificación para anular una evolución clínica.");
        }

        FolioClinico folio = folioRepository.findById(idFolio)
                .orElseThrow(() -> new RuntimeException("Error: El folio clínico no existe."));

        if ("ANULADO".equals(folio.getEstadoFolio())) {
            throw new RuntimeException("Conflicto: Este folio ya se encuentra anulado.");
        }

        folio.setEstadoFolio("ANULADO");
        folio.setJustificacionAnulacion(justificacion);

        return folioRepository.save(folio);
    }
}