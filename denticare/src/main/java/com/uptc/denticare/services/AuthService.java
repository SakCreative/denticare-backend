package com.uptc.denticare.services;

import com.uptc.denticare.dto.LoginRequest;
import com.uptc.denticare.dto.AuthResponse;
import com.uptc.denticare.models.Usuario;
import com.uptc.denticare.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Mapa en memoria para contar intentos fallidos (Requisito del proyecto)
    private final Map<String, Integer> intentosFallidos = new HashMap<>();

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername();

        // 1. VERIFICAR SI ESTA BLOQUEADO POR 3 INTENTOS FALLIDOS
        if (intentosFallidos.getOrDefault(username, 0) >= 3) {
            throw new RuntimeException("HTTP 401: Usuario bloqueado por múltiples intentos fallidos.");
        }

        // 2. Buscar al usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            registrarIntentoFallido(username);
            throw new RuntimeException("HTTP 401: Credenciales inválidas. Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        // 3. Validar la contraseña con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            registrarIntentoFallido(username);
            throw new RuntimeException("HTTP 401: Credenciales inválidas. Contraseña incorrecta.");
        }

        // 4. Si todo está OK, resetear intentos y devolver el Token (simulado por ahora)
        intentosFallidos.remove(username);
        String tokenFalso = "ey.token.jwt.simulado.para.probar.logica";

        return new AuthResponse(tokenFalso, "Login exitoso");
    }

    private void registrarIntentoFallido(String username) {
        int intentos = intentosFallidos.getOrDefault(username, 0);
        intentosFallidos.put(username, intentos + 1);
    }
}