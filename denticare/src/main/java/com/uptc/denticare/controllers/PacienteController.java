package com.uptc.denticare.controllers;
import com.uptc.denticare.models.Paciente;
import com.uptc.denticare.services.PacienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el recurso Paciente (/api/pacientes).
 * Expone operaciones de creación, consulta paginada, detalle y actualización.
 * No se expone borrado físico para preservar la integridad referencial con Cita,
 * HistoriaClinica y Factura.
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Crea un nuevo registro de paciente y retorna HTTP 201 Created.
    @PostMapping
    public ResponseEntity<Paciente> crear(@RequestBody Paciente request) {
        Paciente pacienteCreado = pacienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCreado);
    }

    // Devuelve una página de pacientes ordenada por idPaciente ascendente por defecto.
    @GetMapping
    public ResponseEntity<Page<Paciente>> listar(
            @PageableDefault(size = 10, sort = "idPaciente", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pacienteService.listar(pageable));
    }

    // Obtiene el detalle de un paciente específico por su identificador.
    @GetMapping("/{idPaciente}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(pacienteService.buscarPorId(idPaciente));
    }

    

    // Actualiza los datos permitidos de un paciente existente.
    @PutMapping("/{idPaciente}")
    public ResponseEntity<Paciente> actualizar(
            @PathVariable Long idPaciente,
            @RequestBody Paciente request) {
        return ResponseEntity.ok(pacienteService.actualizar(idPaciente, request));
    }
    @GetMapping("/todos")
    public ResponseEntity<List<Paciente>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos()); // O pacienteRepository.findAll()
}
}