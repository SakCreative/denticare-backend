package com.uptc.denticare.controllers;

import com.uptc.denticare.models.Consultorio;
import com.uptc.denticare.services.ConsultorioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultorios")
public class ConsultorioController {

    private final ConsultorioService consultorioService;

    public ConsultorioController(ConsultorioService consultorioService) {
        this.consultorioService = consultorioService;
    }

    // POST /api/consultorios -> Crea una sala nueva (201 Created)
    @PostMapping
    public ResponseEntity<Consultorio> crear(@RequestBody Consultorio consultorio) {
        Consultorio creado = consultorioService.crear(consultorio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET /api/consultorios?page=0&size=10 -> Lista todos los consultorios paginados (200 OK)
    @GetMapping
    public ResponseEntity<Page<Consultorio>> listar(
            @PageableDefault(size = 10, sort = "idConsultorio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(consultorioService.listarPaginado(pageable));
    }

    // GET /api/consultorios/activos -> Lista rápida de salas disponibles (estado = true)
    @GetMapping("/activos")
    public ResponseEntity<List<Consultorio>> listarActivos() {
        return ResponseEntity.ok(consultorioService.listarPorEstado(true));
    }

    // GET /api/consultorios/{idConsultorio} -> Busca un consultorio por ID
    @GetMapping("/{idConsultorio}")
    public ResponseEntity<Consultorio> obtenerPorId(@PathVariable Long idConsultorio) {
        return ResponseEntity.ok(consultorioService.obtenerPorId(idConsultorio));
    }

    // PUT /api/consultorios/{idConsultorio} -> Actualiza nombre o activa/desactiva el consultorio
    @PutMapping("/{idConsultorio}")
    public ResponseEntity<Consultorio> actualizar(
            @PathVariable Long idConsultorio,
            @RequestBody Consultorio consultorio) {
        return ResponseEntity.ok(consultorioService.actualizar(idConsultorio, consultorio));
    }
}