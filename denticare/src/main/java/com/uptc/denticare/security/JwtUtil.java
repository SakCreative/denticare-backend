package com.uptc.denticare.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
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

    // MÉTODO ORIGINAL
    public String generarTokenReal(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) 
                .compact();
    }

    // MÉTODO  Para guardar el token con el rol
    public String generarTokenReal(String username, String rol) {
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol) // Aquí se incrusta el rol en el token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) 
                .compact();
    }

    // MÉTODO ORIGINAL
    public String extraerUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) 
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // MÉTODO  Extraer el rol del token para el filtro
    public String extraerRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

    // MÉTODO  para que el JwtFilter no genere error
    public boolean validarToken(String token, UserDetails userDetails) {
        String tokenUsername = extraerUsername(token);
        Date expiracion = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
                
        return (tokenUsername.equals(userDetails.getUsername()) && !expiracion.before(new Date()));
    }
}