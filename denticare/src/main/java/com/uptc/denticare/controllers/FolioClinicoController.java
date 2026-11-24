package com.uptc.denticare.controllers;

import com.uptc.denticare.models.FolioClinico;
import com.uptc.denticare.services.FolioClinicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/folios")
public class FolioClinicoController {

    private final FolioClinicoService folioService;

    public FolioClinicoController(FolioClinicoService folioService) {
        this.folioService = folioService;
    }

    // POST: Crear nueva evolución en la historia clínica
    @PostMapping("/historia/{idHistoria}")
    public ResponseEntity<FolioClinico> crear(
            @PathVariable Long idHistoria,
            @RequestBody FolioClinico folio) {
        FolioClinico creado = folioService.crearFolio(idHistoria, folio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET: Listar evoluciones de una historia (las más recientes primero)
    @GetMapping("/historia/{idHistoria}")
    public ResponseEntity<List<FolioClinico>> listarPorHistoria(@PathVariable Long idHistoria) {
        List<FolioClinico> folios = folioService.obtenerFoliosPorHistoria(idHistoria);
        return ResponseEntity.ok(folios);
    }

    // PUT: Anular un folio (Requiere enviar "justificacion" en el JSON)
    @PutMapping("/{idFolio}/anular")
    public ResponseEntity<FolioClinico> anular(
            @PathVariable Long idFolio,
            @RequestBody Map<String, String> payload) {
        String justificacion = payload.get("justificacion");
        FolioClinico anulado = folioService.anularFolio(idFolio, justificacion);
        return ResponseEntity.ok(anulado);
    }
}