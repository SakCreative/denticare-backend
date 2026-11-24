package com.uptc.denticare.services;

import com.uptc.denticare.models.DetalleFactura;
import com.uptc.denticare.models.Factura;
import com.uptc.denticare.models.Paciente;
import com.uptc.denticare.repositories.FacturaRepository;
import com.uptc.denticare.repositories.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PacienteRepository pacienteRepository;
    
    // Tasa de impuesto estándar (19% IVA)
    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.19"); 

    public FacturaService(FacturaRepository facturaRepository, PacienteRepository pacienteRepository) {
        this.facturaRepository = facturaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // 1. Crear Factura con Detalles (Cálculos automáticos línea por línea)
    public Factura crearFactura(Long idPaciente, Factura nuevaFactura) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Error: El paciente no existe en el sistema."));

        if (nuevaFactura.getDetalles() == null || nuevaFactura.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Error Crítico: La factura debe contener al menos un detalle (tratamiento).");
        }

        nuevaFactura.setIdFactura(null);
        nuevaFactura.setPaciente(paciente);
        nuevaFactura.setFechaEmision(LocalDateTime.now());

        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        // Recorrer los ítems para calcular totales y enlazar la relación bidireccional
        for (DetalleFactura detalle : nuevaFactura.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del ítem debe ser mayor a cero.");
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
            }

            // Calcular el subtotal de esa línea (Cantidad * Precio)
            BigDecimal subDetalle = detalle.getPrecioUnitario()
                    .multiply(new BigDecimal(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            
            detalle.setSubtotalDetalle(subDetalle);
            detalle.setFactura(nuevaFactura); // Importante: enlazar el detalle con su factura padre

            // Sumar al subtotal general de la factura
            subtotalCalculado = subtotalCalculado.add(subDetalle);
        }

        nuevaFactura.setSubtotal(subtotalCalculado);

        // Cálculo financiero general (IVA y Total)
        BigDecimal impuestos = nuevaFactura.getSubtotal().multiply(TASA_IMPUESTO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = nuevaFactura.getSubtotal().add(impuestos).setScale(2, RoundingMode.HALF_UP);
        
        nuevaFactura.setImpuestos(impuestos);
        nuevaFactura.setTotal(total);

        // Autogenerar consecutivo legal basado en el total de registros
        long cantidadExistente = facturaRepository.count();
        nuevaFactura.setNumeroFactura("FE-" + String.format("%04d", cantidadExistente + 1));

        // Generar un CUFE provisional para futura integración
        nuevaFactura.setCufe(UUID.randomUUID().toString());

        // Valores administrativos por defecto
        nuevaFactura.setEstadoPago("PENDIENTE");
        nuevaFactura.setEstadoFiscal("NO_ENVIADA");

        return facturaRepository.save(nuevaFactura);
    }

    // 2. Registrar el pago de una factura
    public Factura registrarPago(Long idFactura, String metodoPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("Es obligatorio especificar el método de pago.");
        }

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Error: La factura solicitada no existe."));

        if ("PAGADA".equals(factura.getEstadoPago())) {
            throw new RuntimeException("Conflicto: Esta factura ya se encuentra pagada.");
        }

        factura.setEstadoPago("PAGADA");
        factura.setMetodoPago(metodoPago);

        return facturaRepository.save(factura);
    }

    // 3. Consultar historial del paciente con paginación
    @Transactional(readOnly = true)
    public Page<Factura> obtenerFacturasPorPaciente(Long idPaciente, Pageable pageable) {
        return facturaRepository.findByPaciente_IdPacienteOrderByFechaEmisionDesc(idPaciente, pageable);
    }

    @Transactional(readOnly = true)
    public Factura obtenerPorId(Long idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Error: La factura no existe."));
    }

    public Factura anularFactura(Long idFactura, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Es obligatorio indicar el motivo de la anulación.");
        }
        Factura factura = obtenerPorId(idFactura);
        if ("PAGADA".equals(factura.getEstadoPago())) {
            throw new RuntimeException("Conflicto: Una factura pagada no se puede anular directamente; requiere nota crédito.");
        }
        factura.setEstadoPago("ANULADA");
        return facturaRepository.save(factura);
    }
}