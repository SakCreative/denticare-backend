package com.uptc.denticare.controllers;

import com.uptc.denticare.models.Factura;
import com.uptc.denticare.services.FacturaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    // POST: Generar factura (calcula impuestos y consecutivos automáticamente)
    @PostMapping("/paciente/{idPaciente}")
    public ResponseEntity<Factura> crear(@PathVariable Long idPaciente, @RequestBody Factura factura) {
        Factura creada = facturaService.crearFactura(idPaciente, factura);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // PUT: Registrar el pago de la factura
    @PutMapping("/{idFactura}/pagar")
    public ResponseEntity<Factura> registrarPago(@PathVariable Long idFactura, @RequestBody Map<String, String> payload) {
        String metodoPago = payload.get("metodoPago");
        Factura pagada = facturaService.registrarPago(idFactura, metodoPago);
        return ResponseEntity.ok(pagada);
    }

    // GET: Historial del paciente con paginación (ej: page=0, size=10)
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<Page<Factura>> listarPorPaciente(
            @PathVariable Long idPaciente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Factura> facturas = facturaService.obtenerFacturasPorPaciente(idPaciente, pageable);
        return ResponseEntity.ok(facturas);
    }

    // GET: Listar todas las facturas con paginación (ej: /api/facturas?page=0&size=10)
@GetMapping
public ResponseEntity<Page<Factura>> listarTodas(
        @RequestParam(required = false) String estadoPago,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(facturaService.listarFacturas(estadoPago, pageable));
}

    // GET: Consultar una factura específica por su ID (sin paginación, es un único registro)
    @GetMapping("/{idFactura}")
    public ResponseEntity<Factura> obtenerPorId(@PathVariable Long idFactura) {
        return ResponseEntity.ok(facturaService.obtenerPorId(idFactura));
    }

    // PUT: Anular factura (no se borra de BD por trazabilidad contable)
    @PutMapping("/{idFactura}/anular")
    public ResponseEntity<Factura> anular(
            @PathVariable Long idFactura,
            @RequestBody Map<String, String> payload) {
        String motivo = payload.get("motivo");
        return ResponseEntity.ok(facturaService.anularFactura(idFactura, motivo));
    }
}