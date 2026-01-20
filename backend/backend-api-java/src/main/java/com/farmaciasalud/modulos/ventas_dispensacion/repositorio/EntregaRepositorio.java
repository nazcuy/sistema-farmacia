package com.farmaciasalud.modulos.ventas_dispensacion.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepositorio extends JpaRepository<Entrega, Long> {
    
    List<Entrega> findByPersonaIdOrderByFechaEntregaDesc(Long personaId);
    
    List<Entrega> findByUsuarioEntregaId(Long usuarioId);
    
    @Query("SELECT e FROM Entrega e WHERE e.usuarioEntrega.id = :usuarioId ORDER BY e.fechaEntrega DESC")
    List<Entrega> buscarPorUsuarioEntregaOrdenado(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT e FROM Entrega e WHERE e.fechaEntrega BETWEEN :inicio AND :fin")
    List<Entrega> buscarPorRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT e FROM Entrega e LEFT JOIN FETCH e.detalles WHERE e.id = :id")
    Optional<Entrega> buscarConDetalles(@Param("id") Long id);
    
    @Query("SELECT e FROM Entrega e LEFT JOIN FETCH e.detalles LEFT JOIN FETCH e.persona WHERE e.id = :id")
    Optional<Entrega> buscarCompleta(@Param("id") Long id);
    
    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.usuarioEntrega.id = :usuarioId AND e.fechaEntrega >= :fecha")
    long contarEntregasPorUsuario(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT e FROM Entrega e WHERE e.receta.id IS NOT NULL ORDER BY e.fechaEntrega DESC")
    List<Entrega> buscarConReceta();
    
    @Query("SELECT e FROM Entrega e WHERE e.receta.id IS NULL ORDER BY e.fechaEntrega DESC")
    List<Entrega> buscarSinReceta();
}