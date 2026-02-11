package com.farmaciasalud.nucleo.seguridad;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter

public class CustomUserDetails implements UserDetails {
    
    private final Long id;
    private final String email;
    private final String password;
    private final String nombre;
    private final String apellido;
    private final com.farmaciasalud.nucleo.seguridad.Rol rol;
    private final boolean activo;
    
    public CustomUserDetails(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.password = usuario.getPasswordHash();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.rol = usuario.getRol();
        this.activo = usuario.getActivo();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte el rol de la aplicación a autoridad de Spring Security
        // Ejemplo: "MEDICO" -> "ROLE_MEDICO"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // Las cuentas no expiran por tiempo
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true; // Las cuentas no se bloquean por intentos fallidos en este ejemplo
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }
    
    @Override
    public boolean isEnabled() {
        return activo;
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
