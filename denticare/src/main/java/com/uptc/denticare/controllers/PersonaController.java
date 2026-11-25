package com.uptc.denticare.controllers;

import com.uptc.denticare.models.Persona;
import com.uptc.denticare.services.PersonaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el recurso Persona (/api/personas).
 * Permite crear personas, listarlas paginadas, consultarlas por ID o documento
 * y actualizarlas sin borrado físico.
 */
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // Registra una nueva persona y devuelve HTTP 201 Created.
    @PostMapping
    public ResponseEntity<Persona> crear(@RequestBody Persona request) {
        Persona creada = personaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // Lista personas de forma paginada ordenadas por idPersona ascendente.
    @GetMapping
    public ResponseEntity<Page<Persona>> listar(
            @PageableDefault(size = 10, sort = "idPersona", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(personaService.listar(pageable));
    }

    // Obtiene una persona por su id_persona.
    @GetMapping("/{idPersona}")
    public ResponseEntity<Persona> buscarPorId(@PathVariable Long idPersona) {
        return ResponseEntity.ok(personaService.buscarPorId(idPersona));
    }

    // Busca una persona por su número de documento.
    @GetMapping("/documento/{documento}")
    public ResponseEntity<Persona> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(personaService.buscarPorDocumento(documento));
    }

    // Actualiza los datos de una persona existente.
    @PutMapping("/{idPersona}")
    public ResponseEntity<Persona> actualizar(
            @PathVariable Long idPersona,
            @RequestBody Persona request) {
        return ResponseEntity.ok(personaService.actualizar(idPersona, request));
    }
}