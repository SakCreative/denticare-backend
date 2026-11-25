package com.uptc.denticare.controllers;

import com.uptc.denticare.models.Odontologo;
import com.uptc.denticare.services.OdontologoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/odontologos")
public class OdontologoController {

    private final OdontologoService odontologoService;

    public OdontologoController(OdontologoService odontologoService) {
        this.odontologoService = odontologoService;
    }

    // POST /api/odontologos -> Crea un odontólogo (201 Created)
    @PostMapping
    public ResponseEntity<Odontologo> crear(@RequestBody Odontologo odontologo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(odontologoService.crear(odontologo));
    }

    // GET /api/odontologos?page=0&size=10 -> Lista paginada (200 OK)
    @GetMapping
    public ResponseEntity<Page<Odontologo>> listar(
            @PageableDefault(size = 10, sort = "idOdontologo", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(odontologoService.listarPaginado(pageable));
    }

    // GET /api/odontologos/{idOdontologo} -> Busca por ID (200 OK o 404 Not Found)
    @GetMapping("/{idOdontologo}")
    public ResponseEntity<Odontologo> obtenerPorId(@PathVariable Long idOdontologo) {
        return ResponseEntity.ok(odontologoService.obtenerPorId(idOdontologo));
    }

    // GET /api/odontologos/especialidad?nombre=... -> Filtra por especialidad (200 OK)
    @GetMapping("/especialidad")
    public ResponseEntity<List<Odontologo>> buscarPorEspecialidad(@RequestParam String nombre) {
        return ResponseEntity.ok(odontologoService.buscarPorEspecialidad(nombre));
    }

    // PUT /api/odontologos/{idOdontologo} -> Actualiza datos del odontólogo (200 OK)
    @PutMapping("/{idOdontologo}")
    public ResponseEntity<Odontologo> actualizar(
            @PathVariable Long idOdontologo,
            @RequestBody Odontologo odontologo) {
        return ResponseEntity.ok(odontologoService.actualizar(idOdontologo, odontologo));
    }
}