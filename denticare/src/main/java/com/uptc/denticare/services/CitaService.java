package com.uptc.denticare.services;

import com.uptc.denticare.dto.AgendarCitaDTO;
import com.uptc.denticare.models.Cita;
import com.uptc.denticare.models.Consultorio;
import com.uptc.denticare.models.Odontologo;
import com.uptc.denticare.models.Paciente;
import com.uptc.denticare.models.Persona;
import com.uptc.denticare.repositories.CitaRepository;
import com.uptc.denticare.repositories.PacienteRepository;
import com.uptc.denticare.repositories.PersonaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// Etiqueto la clase con @Service para que Spring sepa que aquí va 
// la inteligencia y las reglas de negocio de las citas.
@Service
public class CitaService {

    // 1. Inyectamos los repositorios necesarios usando 'final'
    private final CitaRepository citaRepository;
    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;

    // El constructor recibe las 3 dependencias para inyectarlas
    public CitaService(CitaRepository citaRepository,
                       PersonaRepository personaRepository,
                       PacienteRepository pacienteRepository) {
        this.citaRepository = citaRepository;
        this.personaRepository = personaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // --- NUEVO MÉTODO UNIFICADO ---
    // @Transactional asegura que si algo falla, no se guarden datos a medias.
    @Transactional
    public Cita agendarCitaCompleta(AgendarCitaDTO dto) {
        
        // A. Buscar o crear la Persona por su documento
        Persona persona = personaRepository.findByDocumento(dto.getDocumento())
            .orElseGet(() -> {
                Persona nueva = new Persona();
                nueva.setDocumento(dto.getDocumento());
                nueva.setNombres(dto.getNombres());
                nueva.setApellidos(dto.getApellidos());
                nueva.setTelefono(dto.getTelefono());
                nueva.setCorreo(dto.getCorreo());
                return personaRepository.save(nueva);
            });

        // B. Buscar o crear el Paciente asociado a esa Persona (usando solo su ID)
        Paciente paciente = pacienteRepository.findByIdPersona(persona.getIdPersona())
            .orElseGet(() -> {
                Paciente nuevo = new Paciente();
                // Aquí pasamos el ID numérico en lugar del objeto completo:
                nuevo.setIdPersona(persona.getIdPersona()); 
                nuevo.setFechaNacimiento(dto.getFechaNacimiento());
                return pacienteRepository.save(nuevo);
            });
            

        // C. Preparar la Cita
        Cita cita = new Cita();
        cita.setPaciente(paciente);

        Odontologo odontologo = new Odontologo();
        odontologo.setIdOdontologo(dto.getIdOdontologo());
        cita.setOdontologo(odontologo);

        Consultorio consultorio = new Consultorio();
        consultorio.setIdConsultorio(dto.getIdConsultorio());
        cita.setConsultorio(consultorio);

        cita.setFechaHoraInicio(dto.getFechaHoraInicio());
        cita.setFechaHoraFin(dto.getFechaHoraFin());
        cita.setMotivo(dto.getMotivo());
        cita.setEstado("PROGRAMADA");

        // D. Guardo la cita usando TU propio método para aprovechar el control de cruces
        return this.agendarCita(cita);
    }

    // --- MÉTODOS ORIGINALES ---

    // Este es el método original que controla la regla estricta (EXCLUDE USING gist)
    public Cita agendarCita(Cita nuevaCita) {
        try {
            return citaRepository.save(nuevaCita);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error al agendar: El horario seleccionado ya está ocupado o se cruza con otra cita del médico.");
        }
    }

    // Atiende el requerimiento de filtrar citas de ortodoncistas, cirujanos, etc.
    public List<Cita> obtenerAgendaPorEspecialidad(String especialidad, LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByOdontologo_EspecialidadAndFechaHoraInicioBetween(especialidad, inicio, fin);
    }

    // UPDATE: Reprogramar una cita
    public Cita actualizarCita(Long idCita, Cita citaActualizada) {
        return citaRepository.findById(idCita).map(citaExistente -> {
            citaExistente.setFechaHoraInicio(citaActualizada.getFechaHoraInicio());
            citaExistente.setFechaHoraFin(citaActualizada.getFechaHoraFin());
            citaExistente.setEstado(citaActualizada.getEstado());
            citaExistente.setMotivo(citaActualizada.getMotivo());
            
            // Reutilizo agendarCita para que valide cruces al reprogramar
            return this.agendarCita(citaExistente);
        }).orElseThrow(() -> new RuntimeException("Cita no encontrada con el ID: " + idCita));
    }

    // DELETE: Cancelar y liberar el horario
    public void eliminarCita(Long idCita) {
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("No se puede eliminar: La cita no existe.");
        }
        // Al borrarla, PostgreSQL libera automáticamente el candado GiST
        citaRepository.deleteById(idCita);
    }

    // Obtener todas las citas de un doctor específico
    public List<Cita> obtenerAgendaOdontologo(Long idOdontologo) {
        return citaRepository.findByOdontologo_IdOdontologo(idOdontologo);
    }

    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }
}