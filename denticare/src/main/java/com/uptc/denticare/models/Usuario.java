package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity 
@Table(name = "usuario")


public class Usuario {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    //Mapea la columna id_persona de la tabla
    @Column(name = "id_persona")
    private Long idPersona;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;
    
}
