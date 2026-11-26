package com.uptc.denticare.controllers;

import com.uptc.denticare.models.Cita;
import com.uptc.denticare.dto.AgendarCitaDTO;
import com.uptc.denticare.services.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

//  Con @RestController 
// Todas las peticiones HTTP que lleguen a /api/citas pasarán por aquí.
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    // METODOS //
    
    // Endpoint tipo POST. (el frontend) enviará 
    // un JSON con los IDs del paciente, odontólogo y las fechas de inicio/fin.
    @PostMapping
    public ResponseEntity<?> agendarNuevaCita(@RequestBody Cita nuevaCita) {
        try {
            Cita citaGuardada = citaService.agendarCita(nuevaCita);
            return ResponseEntity.status(HttpStatus.CREATED).body(citaGuardada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // GET: Buscar disponibilidad por especialidad y fechas
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<Cita>> listarCitasPorEspecialidad(
            @PathVariable String especialidad,
            @RequestParam String inicio,
            @RequestParam String fin) {
        
        LocalDateTime fechaInicio = LocalDateTime.parse(inicio);
        LocalDateTime fechaFin = LocalDateTime.parse(fin);
        
        return ResponseEntity.ok(citaService.obtenerAgendaPorEspecialidad(especialidad, fechaInicio, fechaFin));
    }

    // ---> 1. RUTA FALTANTE AGREGADA: Ver las citas de un doctor específico
    @GetMapping("/odontologo/{idOdontologo}")
    public ResponseEntity<List<Cita>> listarCitasOdontologo(@PathVariable Long idOdontologo) {
        return ResponseEntity.ok(citaService.obtenerAgendaOdontologo(idOdontologo));
    }
    
    // ---> 2. RUTA FALTANTE AGREGADA: Ver absolutamente todas las citas (Ideal para tu prueba)
    @GetMapping("/todas")
    public ResponseEntity<List<Cita>> listarAbsolutamenteTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    // UPDATE: Ruta para modificar una cita (Ej: PUT /api/citas/15)
    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizarCita(@PathVariable Long id, @RequestBody Cita cita) {
        try {
            Cita citaActualizada = citaService.actualizarCita(id, cita);
            return ResponseEntity.ok(citaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE: Ruta para cancelar/borrar (Ej: DELETE /api/citas/15)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCita(@PathVariable Long id) {
        try {
            citaService.eliminarCita(id);
            return ResponseEntity.ok("Cita cancelada y horario liberado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/completa")
public ResponseEntity<?> agendarCitaCompleta(@RequestBody AgendarCitaDTO dto) {
    try {
        Cita citaGuardada = citaService.agendarCitaCompleta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(citaGuardada);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
}