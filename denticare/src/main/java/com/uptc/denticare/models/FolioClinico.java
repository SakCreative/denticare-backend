package com.uptc.denticare.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "folio_clinico")
public class FolioClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_folio")
    private Long idFolio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "evolucion", columnDefinition = "TEXT", nullable = false)
    private String evolucion;

    // Firma requerida 
    @Column(name = "firma_odontologo", nullable = false)
    private String firmaOdontologo; 

    // Estado del folio (ej: "ACTIVO", "ANULADO")
    @Column(name = "estado_folio", nullable = false, length = 20)
    private String estadoFolio;

    // 
    @Column(name = "justificacion_anulacion", columnDefinition = "TEXT")
    private String justificacionAnulacion;

    // Relación N:1 con Historia Clínica (Muchos folios pertenecen a 1 historia)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia", nullable = false)
    private HistoriaClinica historiaClinica;
}