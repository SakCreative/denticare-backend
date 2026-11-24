package com.uptc.denticare.models;

import jakarta.persistence.*;
import lombok.Data;

//Representacion en java de la tabla fisica odontologo en PostgrSQL.
@Entity 
@Table(name = "odontologo")
//Lombok inyecta autom. los getters y setters en tiempo de compilacion
@Data 

public class Odontologo {
    //@Id Indentificacion llave primaria 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_odontologo")
    private Long idOdontologo;

    //Relacion One-to-one implicita: Me vinculo con el ID de la tabla Usuario
    //para saver con que credenciales (JWT) inicia sesion este medico.

    @Column(name = "id_usuario", unique = true, nullable = false)
    private Long idUsuario;

    //Almaceno la tarjeta profesional del medico. Restrinjo la long a 40
    //caracteres para proteger la db de datos de desbordamientos.
    @Column(name = "registro_medico", unique = true, nullable = false, length = 40)
    private String registroMedico;

    //NUEVO CAMPO: Agregado para categorizar el tipo de especialista
    //(ej. Ortodoncista, Cirujano Maxilofacial, Odontologo Gral.)
    @Column(name = "especialidad", nullable = false, length = 50)
    private String especialidad;
    

}
