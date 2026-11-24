package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "consultorio")
public class Consultorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consultorio")
    private Long idConsultorio;

    @Column(name = "nombre_sala")
    private String nombreSala;

    @Column(name = "estado")
    private Boolean estado; 
}