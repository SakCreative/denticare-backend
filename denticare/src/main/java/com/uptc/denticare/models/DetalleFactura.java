package com.uptc.denticare.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_factura")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    // Código interno o estándar del servicio (Ej: "PR-01" para Profilaxis). Obligatorio para factura electrónica DIAN.
    @Column(name = "codigo_item", nullable = false, length = 50)
    private String codigoItem;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    // Calculado automáticamente: cantidad * precio_unitario
    @Column(name = "subtotal_detalle", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalDetalle;

    // Relación N:1 con Factura (Muchos ítems/detalles pertenecen a 1 sola factura)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;
}