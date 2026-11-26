package com.uptc.denticare.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AgendarCitaDTO {
    // Datos mínimos del paciente
    private String documento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private LocalDate fechaNacimiento;

    // Datos de la cita
    private Long idOdontologo;
    private Long idConsultorio;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String motivo;
}