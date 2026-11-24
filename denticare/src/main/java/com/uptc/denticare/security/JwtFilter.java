package com.uptc.denticare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 1. Buscamos el token en la cabecera 'Authorization' de Postman
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Verificamos que exista y empiece con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Quitamos la palabra "Bearer "
            try {
                username = jwtUtil.extraerUsername(jwt); // Intentamos leerlo
            } catch (Exception e) {
                System.out.println("Token inválido o manipulado");
            }
        }

        // 3. Si el token es válido y el usuario aún no está logueado en este request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Creamos un pase de acceso temporal para Spring Security
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, new ArrayList<>());
            
            // Le decimos a Spring: "Este usuario ya pasó seguridad, déjalo entrar"
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 4. Continuamos con el viaje de la petición hacia el controlador
        chain.doFilter(request, response);
    }
}