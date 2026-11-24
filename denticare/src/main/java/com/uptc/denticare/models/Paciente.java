package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

//es la representacion en Java de la tabla fisica "paciente" en db.
@Entity 
//esta nos indica a SpringBoot  exactamente como se llama en SQL 
@Table(name = "paciente")
//@Data: usamos Lombok para encargarse de los gets and stters 
@Data 

public class Paciente {
    // @Id: Me identifico como la Llave Primaria (Primary Key) de esta tabla.
    @Id
    // GenerationType.IDENTITY: Le digo a Java que deje que PostgreSQL genere mi ID automáticamente (el famoso tipo SERIAL).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    // Me vinculo con el ID de la tabla "persona" (donde guardaremos el nombre, cédula, etc.).
    // Soy unique = true porque un paciente solo puede estar ligado a una persona física.
    @Column(name = "id_persona", unique = true, nullable = false)
    private Long idPersona;

    // LocalDate: Soy la clase moderna de Java para manejar fechas. 
    // Al guardar en PostgreSQL, me convierto nativamente al tipo DATE (formato YYYY-MM-DD), sin incluir las horas.
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
}

    

