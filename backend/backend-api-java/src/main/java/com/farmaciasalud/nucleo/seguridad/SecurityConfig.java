package com.farmaciasalud.nucleo.seguridad;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.nucleo.seguridad.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configuración CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Deshabilitamos CSRF porque usamos JWT (stateless)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configuración de autorización de requests
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (no requieren autenticación)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Endpoints de solo lectura (autenticación requerida)
                .requestMatchers(HttpMethod.GET, "/api/medicamentos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("MEDICO", "FARMACEUTICO", "ADMINISTRADOR", "PROMOTOR")
                
                // Endpoints de escritura (solo roles específicos)
                .requestMatchers("/api/pacientes/**").hasAnyRole("MEDICO", "FARMACEUTICO", "ADMINISTRADOR", "PROMOTOR")
                .requestMatchers("/api/inventario/**").hasAnyRole("FARMACEUTICO", "ADMINISTRADOR")
                .requestMatchers("/api/dispensaciones/**").hasAnyRole("FARMACEUTICO", "PROMOTOR")
                .requestMatchers("/api/historias-clinicas/**").hasAnyRole("MEDICO", "ADMINISTRADOR")
                .requestMatchers("/api/recetas/**").hasAnyRole("MEDICO", "FARMACEUTICO", "ADMINISTRADOR")
                
                // Endpoints de administración (solo administrador)
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                
                // Cualquier otro request requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configuración del proveedor de autenticación
            .authenticationProvider(authenticationProvider())
            
            // Configuración de sesión (stateless para JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Agregar el filtro de JWT antes del filtro de autenticación de username/password
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}