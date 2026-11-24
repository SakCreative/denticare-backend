package com.uptc.denticare.services;

import com.uptc.denticare.models.Cita;
import com.uptc.denticare.repositories.CitaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

//  Etiqueto la clase con @Service para que Spring sepa que aquí va 
// la inteligencia y las reglas de negocio de las citas.
@Service
public class CitaService {

    // Inyecto el repositorio. Uso 'final' como buena práctica para asegurar 
    // que la conexión a datos no se modifique en medio de la ejecución.
    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    //  Este es el método que llamaré desde el controlador cuando 
    // la recepcionista intente agendar una nueva cita.
    public Cita agendarCita(Cita nuevaCita) {
        
        //
        // Envuelvo el guardado en un try-catch porque sé que PostgreSQL tiene 
        // una regla estricta (EXCLUDE USING gist) que prohíbe citas superpuestas.
        try {
            // Intento guardar la cita. Si el horario está libre, PostgreSQL lo acepta.
            return citaRepository.save(nuevaCita);
            
        } catch (DataIntegrityViolationException e) {
            // Si el horario se cruza, PostgreSQL rechaza el INSERT.
            // Spring Boot traduce ese rechazo en una DataIntegrityViolationException.
            // Aquí atrapo ese error exacto y lanzo mi propio mensaje controlado, 
            // evitando que la pantalla de la recepcionista se llene de código rojo.
            
            throw new RuntimeException("Error al agendar: El horario seleccionado ya está ocupado o se cruza con otra cita del médico.");
        }
    }

    // ---  (CALENDARIO POR ESPECIALIDAD) ---
    // Atiende el requerimiento de filtrar citas de ortodoncistas, cirujanos, etc., en fechas específicas.
    public List<Cita> obtenerAgendaPorEspecialidad(String especialidad, LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByOdontologo_EspecialidadAndFechaHoraInicioBetween(especialidad, inicio, fin);
    }

    // --- UPDATE: Reprogramar una cita ---
    public Cita actualizarCita(Long idCita, Cita citaActualizada) {
        // 1. Buscamos si la cita existe
        return citaRepository.findById(idCita).map(citaExistente -> {
            // 2. Actualizamos los datos permitidos
            citaExistente.setFechaHoraInicio(citaActualizada.getFechaHoraInicio());
            citaExistente.setFechaHoraFin(citaActualizada.getFechaHoraFin());
            citaExistente.setEstado(citaActualizada.getEstado());
            citaExistente.setMotivo(citaActualizada.getMotivo());
            
            // 3. Guardo. Si la nueva hora choca con otra, 
            // el EXCLUDE de PostgreSQL lanzará la excepción que ya controlamos.
            return citaRepository.save(citaExistente);
        }).orElseThrow(() -> new RuntimeException("Cita no encontrada con el ID: " + idCita));
    }

    // --- DELETE: Cancelar y liberar el horario ---
    public void eliminarCita(Long idCita) {
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("No se puede eliminar: La cita no existe.");
        }
        // Al borrarla, PostgreSQL libera automáticamente el candado GiST de ese horario
        citaRepository.deleteById(idCita);
    }

    // Método para obtener todas las citas de un doctor específico
    public List<Cita> obtenerAgendaOdontologo(Long idOdontologo) {
        return citaRepository.findByOdontologo_IdOdontologo(idOdontologo);
    }

    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }


}