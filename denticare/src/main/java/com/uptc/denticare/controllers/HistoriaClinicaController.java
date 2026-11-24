package com.uptc.denticare.controllers;

import com.uptc.denticare.models.HistoriaClinica;
import com.uptc.denticare.services.HistoriaClinicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historias-clinicas")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaClinicaService;

    public HistoriaClinicaController(HistoriaClinicaService historiaClinicaService) {
        this.historiaClinicaService = historiaClinicaService;
    }

    // POST: Crear historia clínica para un paciente existente
    @PostMapping("/paciente/{idPaciente}")
    public ResponseEntity<HistoriaClinica> crear(
            @PathVariable Long idPaciente,
            @RequestBody HistoriaClinica historiaClinica) {
        HistoriaClinica creada = historiaClinicaService.crearHistoriaClinica(idPaciente, historiaClinica);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // GET: Consultar historia clínica por ID de paciente (Solo trae si está activa)
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<HistoriaClinica> obtenerPorPaciente(@PathVariable Long idPaciente) {
        HistoriaClinica historia = historiaClinicaService.obtenerPorIdPaciente(idPaciente);
        return ResponseEntity.ok(historia);
    }

    // PUT: Actualizar historia clínica (alergias, tipo de sangre, etc.)
    @PutMapping("/paciente/{idPaciente}")
    public ResponseEntity<HistoriaClinica> actualizar(
            @PathVariable Long idPaciente,
            @RequestBody HistoriaClinica datosActualizados) {
        HistoriaClinica actualizada = historiaClinicaService.actualizarHistoriaClinica(idPaciente, datosActualizados);
        return ResponseEntity.ok(actualizada);
    }

    // DELETE: Borrado lógico de la historia clínica (ISO 27001)
    @DeleteMapping("/paciente/{idPaciente}")
    public ResponseEntity<Void> deshabilitar(@PathVariable Long idPaciente) {
        historiaClinicaService.deshabilitarHistoriaClinica(idPaciente);
        return ResponseEntity.noContent().build(); // Retorna 204 (operación exitosa sin cuerpo de respuesta)
    }
}