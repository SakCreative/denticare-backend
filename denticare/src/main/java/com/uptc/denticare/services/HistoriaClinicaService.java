package com.uptc.denticare.services;

import com.uptc.denticare.models.HistoriaClinica;
import com.uptc.denticare.models.Paciente;
import com.uptc.denticare.repositories.HistoriaClinicaRepository;
import com.uptc.denticare.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteRepository pacienteRepository;

    public HistoriaClinicaService(HistoriaClinicaRepository historiaRepository,
                                  PacienteRepository pacienteRepository) {
        this.historiaRepository = historiaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public HistoriaClinica crearHistoriaClinica(Long idPaciente, HistoriaClinica nuevaHistoria) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Error: El paciente con ID " + idPaciente + " no existe."));

        // Se usa el nuevo método que busca solo historias ACTIVAS
        if (historiaRepository.existsByPaciente_IdPacienteAndEstadoHistoriaTrue(idPaciente)) {
            throw new RuntimeException("Conflicto: Este paciente ya tiene una historia clínica activa.");
        }

        nuevaHistoria.setIdHistoria(null); // Evita que fuercen un ID manual al crear
        nuevaHistoria.setPaciente(paciente);
        nuevaHistoria.setFechaCreacion(LocalDate.now());
        nuevaHistoria.setEstadoHistoria(true); // Asegurar que nace activa

        return historiaRepository.save(nuevaHistoria);
    }

    @Transactional(readOnly = true)
    public HistoriaClinica obtenerPorIdPaciente(Long idPaciente) {
        if (!pacienteRepository.existePacientePorId(idPaciente)) {
            throw new RuntimeException("Error: El paciente con ID " + idPaciente + " no existe.");
        }

        // Se usa el nuevo método que busca solo historias ACTIVAS
        return historiaRepository.findByPaciente_IdPacienteAndEstadoHistoriaTrue(idPaciente)
                .orElseThrow(() -> new RuntimeException("Error: El paciente con ID " + idPaciente + " no tiene historia clínica activa."));
    }

    public HistoriaClinica actualizarHistoriaClinica(Long idPaciente, HistoriaClinica datosActualizados) {
        HistoriaClinica existente = obtenerPorIdPaciente(idPaciente);

        existente.setTipoSangre(datosActualizados.getTipoSangre());
        existente.setAlergias(datosActualizados.getAlergias());
        existente.setEnfermedadesPrevias(datosActualizados.getEnfermedadesPrevias());

        return historiaRepository.save(existente);
    }

    // NUEVO MÉTODO: Borrado lógico exigido por el profesor (ISO 27001)
    public void deshabilitarHistoriaClinica(Long idPaciente) {
        HistoriaClinica existente = obtenerPorIdPaciente(idPaciente);
        existente.setEstadoHistoria(false); // Apaga la historia en vez de hacer un DELETE en BD
        historiaRepository.save(existente);
    }
}