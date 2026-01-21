package com.farmaciasalud.modulos.pacientes.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import com.farmaciasalud.nucleo.seguridad.Rol;
import com.farmaciasalud.modulos.pacientes.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServicio {
    
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Busca un usuario por email para login.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmailAndActivoTrue(email);
    }
    
    /**
     * Lista usuarios por rol.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepositorio.buscarActivosPorRol(rol);
    }
    
    /**
     * Lista todos los usuarios activos.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findByActivoTrue();
    }
    
    /**
     * Registra un nuevo usuario.
     * Encripta la contraseña antes de guardar.
     */
    public Usuario registrar(String email, String password, String nombre, 
                            String apellido, Rol rol) {
        // Validar email único
        if (usuarioRepositorio.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con email: " + email);
        }
        
        // Encriptar contraseña
        String passwordEncriptado = passwordEncoder.encode(password);
        
        Usuario usuario = Usuario.builder()
                .email(email)
                .passwordHash(passwordEncriptado)
                .nombre(nombre)
                .apellido(apellido)
                .rol(rol)
                .build();
        
        return usuarioRepositorio.save(usuario);
    }
    
    /**
     * Actualiza el último login de un usuario.
     */
    public void actualizarUltimoLogin(Long usuarioId) {
        usuarioRepositorio.actualizarUltimoLogin(usuarioId, LocalDateTime.now());
    }
    
    /**
     * Desactiva un usuario.
     */
    public void desactivar(Long id) {
        usuarioRepositorio.desactivar(id);
    }
    
    /**
     * Verifica la contraseña.
     */
    public boolean verificarPassword(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        return passwordEncoder.matches(password, usuario.getPasswordHash());
    }
}