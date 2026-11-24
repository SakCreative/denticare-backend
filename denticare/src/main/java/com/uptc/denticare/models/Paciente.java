package com.uptc.denticare.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

// Representación en Java de la tabla "paciente" en PostgreSQL.
@Data
@Entity
@Table(name = "paciente")
public class Paciente {

    // Llave primaria autoincremental en PostgreSQL (IDENTITY / SERIAL).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    // Referencia a la tabla "persona" (nombre, documento, contacto, etc.).
    // unique = true garantiza relación 1:1 con persona y nullable = false exige integridad.
    @Column(name = "id_persona", unique = true, nullable = false)
    private Long idPersona;

    // Fecha de nacimiento sin componente horario (DATE en PostgreSQL).
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Relación 1:1 bidireccional con HistoriaClinica.
    // @JsonIgnore evita recursión infinita al serializar JSON (Paciente -> HistoriaClinica -> Paciente).
    @JsonIgnore
    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL)
    private HistoriaClinica historiaClinica;

    // Relación 1:N con Cita (consistente con el JSON de citas que ya probaste).
    @JsonIgnore
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Cita> citas;
}