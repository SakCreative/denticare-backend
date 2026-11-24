package com.uptc.denticare.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController 
@RequestMapping("/api/prueba")


public class PruebaController {
    @GetMapping("/conexion")
    public Map<String, String> probarConexion() {
        return Map.of(
            "status", "success",
            "mensaje", "Backend de Denticare conectado y funcionando perfectamente!"

        );
    }

}
