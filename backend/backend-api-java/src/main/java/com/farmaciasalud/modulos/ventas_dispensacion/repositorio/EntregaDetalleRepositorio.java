package com.farmaciasalud.modulos.ventas_dispensacion.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.EntregaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntregaDetalleRepositorio extends JpaRepository<EntregaDetalle, Long> {
    
    List<EntregaDetalle> findByEntregaId(Long entregaId);
    
    @Query("SELECT ed FROM EntregaDetalle ed LEFT JOIN FETCH ed.lote WHERE ed.entrega.id = :entregaId")
    List<EntregaDetalle> buscarConLote(@Param("entregaId") Long entregaId);
    
    @Query("SELECT ed FROM EntregaDetalle ed WHERE ed.lote.id = :loteId")
    List<EntregaDetalle> buscarPorLote(@Param("loteId") Long loteId);
    
    @Query("SELECT SUM(ed.cantidadEntregada) FROM EntregaDetalle ed WHERE ed.lote.id = :loteId")
    Integer totalEntregadoPorLote(@Param("loteId") Long loteId);
    
    @Query("SELECT ed FROM EntregaDetalle ed JOIN ed.entrega e WHERE e.persona.id = :personaId")
    List<EntregaDetalle> buscarPorPersona(@Param("personaId") Long personaId);
}