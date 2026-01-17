package com.farmaciasalud.modulos.pacientes.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import com.farmaciasalud.nucleo.seguridad.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    
    List<Usuario> findByRol(Rol rol);
    
    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByActivoFalse();
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    List<Usuario> buscarActivosPorRol(@Param("rol") Rol rol);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    long contarPorRol(@Param("rol") Rol rol);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoLogin = :fecha WHERE u.id = :id")
    void actualizarUltimoLogin(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.activo = false WHERE u.id = :id")
    void desactivar(@Param("id") Long id);
    
    @Query("SELECT u FROM Usuario u WHERE u.email LIKE %:dominio%")
    List<Usuario> buscarPorDominioEmail(@Param("dominio") String dominio);
}
