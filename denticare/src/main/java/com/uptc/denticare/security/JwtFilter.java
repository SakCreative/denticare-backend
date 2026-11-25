package com.uptc.denticare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

        // 1. Buscamos el token en la cabecera 'Authorization'
        final String authHeader = request.getHeader("Authorization");

        // 2. Verificamos que exista la cabecera y tenga el prefijo "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7).trim();

            // 3. Validamos firma y expiración del token usando JwtUtil
            if (!jwt.isEmpty() && jwtUtil.validarToken(jwt)) {
                String username = jwtUtil.extraerUsername(jwt);

                // Si el usuario aún no tiene autenticación en este request, la registramos
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null, new ArrayList<>());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else {
                // Token inválido, expirado o manipulado: limpiamos cualquier contexto previo
                SecurityContextHolder.clearContext();
            }
        }

        // 4. Continuamos con la cadena de filtros hacia el controlador o Spring Security
        chain.doFilter(request, response);
    }
}