package com.uptc.denticare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.uptc.denticare.security.JwtFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Marca la clase como una fuente de definiciones de "Beans"(componentes) para el contexto de Spring.
@Configuration 
//@EnableWeb : Habilita la integracion de seguridad web personalizada de spring security
@EnableWebSecurity  
public class SecurityConfig {

    // ---> 1. AGREGADO: Declaramos e inyectamos nuestro filtro 
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    //bean: Indica que este metodo devuelve un objeto que Spring debe administrar y usar globalmente 
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        //Deshabilitamos CSRF (Cross-Site Request Forgery) porque en APIs REST sin estado 
            // (stateless) que usan tokens, esta protección de sesiones no aplica.
            .csrf(AbstractHttpConfigurer::disable)

            // Configuramos las reglas de autorización para las peticiones HTTP:
            .authorizeHttpRequests(auth -> auth
                // permitAll(): Permite el acceso sin autenticación a cualquier ruta de autenticación.
                .requestMatchers("/api/auth/**").permitAll()
                
                // 3. Cualquier otra ruta exigirá token (ESTA REGLA SIEMPRE DEBE IR AL FINAL)
                .anyRequest().authenticated()
            ) 
            
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Construye y aplica la cadena de filtros de seguridad.
        return http.build();
    }
}