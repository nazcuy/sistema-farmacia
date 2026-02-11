package com.farmaciasalud.nucleo.config;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class PasswordEncoderConfig {
        
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt es el algoritmo estándar recomendado para hashear contraseñas
        // El factor de trabajo por defecto es 10 (buen balance entre seguridad y rendimiento)
        return new BCryptPasswordEncoder();
    }
}
