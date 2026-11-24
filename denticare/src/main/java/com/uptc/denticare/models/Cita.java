package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// @Entity: es modelo central del sistema. Represento la tabla "cita" en PostgreSQL.
@Entity
@Table(name = "cita")
@Data
public class Cita {

    // @Id:  Llave Primaria autonumérica.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    // @ManyToOne: "Muchos a Uno". Muchas citas pueden pertenecer a un solo paciente.
    // FetchType.LAZY (Perezoso): Solo voy a la base de datos a traer los datos del 
    // paciente 
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn: llave foránea (Foreign Key) física en la tabla "cita".
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // lo mismo que arriba, pero vinculando la cita con el odontólogo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_odontologo", nullable = false)
    private Odontologo odontologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consultorio", nullable = false )
    private Consultorio consultorio;

    // LocalDateTime: formato moderno y preciso para fechas con horas y minutos.
    //  tipo de dato ideal para que PostgreSQL pueda evaluar tu regla 'EXCLUDE USING gist' 
    // y rechazar rangos de tiempo que se crucen.
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    // Guardo el estado actual (ej. 'PROGRAMADA', 'COMPLETADA', 'CANCELADA').
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    
    // Guardo la razón de la visita o notas breves de la recepcionista.
    @Column(name = "motivo", length = 255)
    private String motivo;
}