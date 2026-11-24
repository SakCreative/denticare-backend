package com.uptc.denticare.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Llave secreta obligatoria (debe tener más de 256 bits / 32 caracteres)
    private static final String SECRET = "DentiCareSecureKey2026ParaGeneracionDeTokensJWT123456";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Tiempo de expiración del token (Ej: 10 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    public String generarTokenReal(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) 
                .compact();
    }

    // Este método desencripta el token y saca el nombre del usuario
    public String extraerUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Usa la misma llave secreta
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}