package com.uptc.denticare.controllers;

import com.uptc.denticare.dto.LoginRequest;
import com.uptc.denticare.dto.AuthResponse;
import com.uptc.denticare.services.AuthService;
import com.uptc.denticare.security.JwtUtil; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/auth") 
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil; //  Declaramos la dependencia del generador de tokens

    //  Inyectamos ambas herramientas por constructor .
    public AuthController(AuthService authService, JwtUtil jwtUtil){
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest request){
        try {
            //  Delegamos la validación de seguridad a tu servicio.
            // Si la contraseña o el usuario están mal, el servicio arrojará un error 
            // y el código saltará automáticamente al bloque 'catch' de abajo.
            authService.login(request); 

            // Procedemos a fabricar el token criptográfico firmado para este usuario en específico.
            String tokenReal = jwtUtil.generarTokenReal(request.getUsername());

            //  Sobrescribimos cualquier texto viejo y armamos la respuesta oficial con el JWT de producción
            AuthResponse respuestaOficial = new AuthResponse(tokenReal, "Login exitoso");
            return ResponseEntity.ok(respuestaOficial);

        } catch(RuntimeException e) {
            //  El escudo: Si hubo fraude o credenciales erróneas, devolvemos un 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}   