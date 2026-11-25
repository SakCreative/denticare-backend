package com.uptc.denticare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Llave secreta obligatoria (mínimo 256 bits / 32 caracteres para HS256)
    private static final String SECRET = "DentiCareSecureKey2026ParaGeneracionDeTokensJWT123456";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Tiempo de expiración del token: 10 horas en milisegundos
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 10;

    // Genera un token JWT firmado con el username y fecha de expiración
    public String generarTokenReal(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(key)
                .compact();
    }

    // Parsea el token y verifica automáticamente su firma y vigencia
    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extrae el nombre de usuario (subject) si el token es válido
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    // Extrae la fecha de expiración del token
    public Date extraerExpiracion(String token) {
        return extraerClaims(token).getExpiration();
    }

    // Verifica de forma segura si el token tiene firma válida y no ha expirado
    public boolean validarToken(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getSubject() != null
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            // El token superó las 10 horas de vigencia
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // Token malformado, vacío o con firma alterada
            return false;
        }
    }
}