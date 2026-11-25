package com.uptc.denticare.services;

import com.uptc.denticare.dto.LoginRequest;
import com.uptc.denticare.dto.AuthResponse;
import com.uptc.denticare.models.Usuario;
import com.uptc.denticare.repositories.UsuarioRepository;
import com.uptc.denticare.security.JwtUtil; // Asegúrate de que la ruta importe tu JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // NUEVO

    private final Map<String, Integer> intentosFallidos = new HashMap<>();

    // Inyectamos JwtUtil en el constructor
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername();

        if (intentosFallidos.getOrDefault(username, 0) >= 3) {
            throw new RuntimeException("HTTP 401: Usuario bloqueado por múltiples intentos fallidos.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            registrarIntentoFallido(username);
            throw new RuntimeException("HTTP 401: Credenciales inválidas. Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            registrarIntentoFallido(username);
            throw new RuntimeException("HTTP 401: Credenciales inválidas. Contraseña incorrecta.");
        }

        intentosFallidos.remove(username);

        // NUEVO: Generamos el token real pasándole el rol, asumiendo un rol por defecto si es nulo
        String rolUsuario = usuario.getRol() != null ? usuario.getRol() : "RECEPCION";
        String tokenReal = jwtUtil.generarTokenReal(usuario.getUsername(), rolUsuario);

        return new AuthResponse(tokenReal, "Login exitoso");
    }

    private void registrarIntentoFallido(String username) {
        int intentos = intentosFallidos.getOrDefault(username, 0);
        intentosFallidos.put(username, intentos + 1);
    }
}