package com.farmaciasalud.modulos.ventas_dispensacion.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepositorio extends JpaRepository<Receta, Long> {
    
    List<Receta> findByPersonaIdOrderByFechaEmisionDesc(Long personaId);
    
    List<Receta> findByMedicoId(Long medicoId);
    
    List<Receta> findByActivaTrue();
    
    List<Receta> findByActivaTrueOrderByFechaEmisionDesc();
    
    @Query("SELECT r FROM Receta r WHERE r.persona.id = :personaId AND r.activa = true AND r.fechaEmision >= :fecha")
    List<Receta> buscarVigentesPorPersona(@Param("personaId") Long personaId, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Receta r WHERE r.activa = true AND r.fechaEmision < :fecha")
    List<Receta> buscarVencidas(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Receta r LEFT JOIN FETCH r.detalles WHERE r.id = :id")
    Optional<Receta> buscarConDetalles(@Param("id") Long id);
    
    @Query("SELECT r FROM Receta r LEFT JOIN FETCH r.detalles LEFT JOIN FETCH r.persona WHERE r.id = :id")
    Optional<Receta> buscarCompleta(@Param("id") Long id);
    
    @Query("SELECT COUNT(r) FROM Receta r WHERE r.medico.id = :medicoId AND r.fechaEmision >= :fecha")
    long contarRecetasMedico(@Param("medicoId") Long medicoId, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Receta r WHERE r.recetaOriginal.id IS NOT NULL")
    List<Receta> buscarRenovaciones();
}