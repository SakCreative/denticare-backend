package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistoria;

    @Column(name = "tipo_sangre", length = 5)
    private String tipoSangre;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "enfermedades_previas", columnDefinition = "TEXT")
    private String enfermedadesPrevias;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    //borrado logico(cumpliendo con la ISO 27001
    @Column(name = "estado_historia", nullable = false)
    private Boolean estadoHistoria = true;

    @OneToOne
    @JoinColumn(name = "id_paciente", referencedColumnName = "id_paciente", unique = true)
    private Paciente paciente;
}