package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;

// Representación en Java de la tabla "persona" en PostgreSQL.
// Centraliza los datos básicos y de contacto compartidos por pacientes, odontólogos y usuarios.
@Data
@Entity
@Table(name = "persona")
public class Persona {

    // Llave primaria autoincremental en PostgreSQL (IDENTITY / SERIAL).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    // Número de documento de identidad (único para evitar duplicados de persona).
    @Column(name = "documento", unique = true, nullable = false, length = 30)
    private String documento;

    // Nombres de la persona.
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    // Apellidos de la persona.
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    // Teléfono de contacto.
    @Column(name = "telefono", length = 30)
    private String telefono;

    // Correo electrónico de contacto.
    @Column(name = "correo", length = 120)
    private String correo;
}