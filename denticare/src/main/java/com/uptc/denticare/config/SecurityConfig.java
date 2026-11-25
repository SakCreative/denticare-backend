package com.uptc.denticare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.uptc.denticare.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;

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

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            // Buscamos en PostgreSQL usando tu repositorio
            com.uptc.denticare.models.Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en la BD: " + username));

            // Extraemos su rol (o le damos uno por defecto) y le agregamos el prefijo
            String rol = usuario.getRol() != null ? usuario.getRol() : "RECEPCION";
            String rolConPrefijo = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

            // Convertimos tu modelo al formato que Spring Security entiende
            return User.withUsername(usuario.getUsername())
                    .password(usuario.getPasswordHash())
                    .authorities(new SimpleGrantedAuthority(rolConPrefijo))
                    .build();
        };
    }  

    //bean: Indica que este metodo devuelve un objeto que Spring debe administrar y usar globalmente 
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF
            .csrf(AbstractHttpConfigurer::disable)
            
            // --- Habilitar CORS para que tu interfaz gráfica pueda conectarse
            .cors(Customizer.withDefaults())
            //  Decirle a Spring que no guarde sesiones en memoria (es vital para JWT)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configuramos las reglas de autorización para las peticiones HTTP:
            .authorizeHttpRequests(auth -> auth
                // permitAll(): Permite el acceso sin autenticación a la ruta de login.
                .requestMatchers("/api/auth/**").permitAll()
                
                // - Proteger la anulación de facturas para que solo el ADMIN pueda
                .requestMatchers(HttpMethod.PUT, "/api/facturas/*/anular").hasAuthority("ROLE_ADMIN")
                
                // 3. Cualquier otra ruta exigirá token (ESTA REGLA SIEMPRE DEBE IR AL FINAL)
                .anyRequest().authenticated()
            ) 
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Construye y aplica la cadena de filtros de seguridad.
        return http.build();
    }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}