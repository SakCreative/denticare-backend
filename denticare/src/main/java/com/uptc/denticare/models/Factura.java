package com.uptc.denticare.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.ArrayList;
@Data
@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long idFactura;

    // Consecutivo legal de la clínica (Ej: FE-1001)
    @Column(name = "numero_factura", unique = true, length = 50)
    private String numeroFactura;

    // Código Único de Facturación Electrónica (Preparado para la DIAN/Clarisa)
    @Column(name = "cufe", unique = true, length = 150)
    private String cufe;

    // Estado ante la entidad fiscal: "NO_ENVIADA", "ACEPTADA_DIAN", "RECHAZADA"
    @Column(name = "estado_fiscal", length = 50)
    private String estadoFiscal;
    // --------------------------------

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    // Detalle de lo que se está cobrando (ej: "Profilaxis y Resina en pieza 14")
    @Column(name = "concepto", nullable = false, columnDefinition = "TEXT")
    private String concepto;

    // Usamos BigDecimal para manejar dinero con precisión exacta (ideal para integrar facturación electrónica)
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    // Estados de control administrativo: "PENDIENTE", "PAGADA", "ANULADA"
    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago;

    // Ej: "EFECTIVO", "TARJETA_CREDITO", "TRANSFERENCIA"
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    // Relación N:1 con Paciente (A quién se le cobra)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "historiasClinicas", "citas"})
    private Paciente paciente;

    // Relación 1:N con DetalleFactura (Una factura tiene muchos ítems)
    // CascadeType.ALL asegura que al guardar la factura, se guarden sus detalles automáticamente
    // orphanRemoval = true borra los detalles si se eliminan de la lista
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles = new ArrayList<>();
}