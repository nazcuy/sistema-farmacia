package com.farmaciasalud.modulos.inventario.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.inventario.dominio.MovimientoStock;
import com.farmaciasalud.modulos.inventario.dominio.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoStockRepositorio extends JpaRepository<MovimientoStock, Long> {
    
    List<MovimientoStock> findByLoteIdOrderByFechaMovimientoDesc(Long loteId);
    
    List<MovimientoStock> findByTipoMovimiento(TipoMovimiento tipo);
    
    @Query("SELECT m FROM MovimientoStock m WHERE m.lote.id = :loteId ORDER BY m.fechaMovimiento DESC")
    List<MovimientoStock> buscarPorLoteOrdenado(@Param("loteId") Long loteId);
    
    @Query("SELECT m FROM MovimientoStock m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin ORDER BY m.fechaMovimiento DESC")
    List<MovimientoStock> buscarPorRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT m FROM MovimientoStock m WHERE m.usuario.id = :usuarioId ORDER BY m.fechaMovimiento DESC")
    List<MovimientoStock> buscarPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT m FROM MovimientoStock m WHERE m.referenciaTabla = :tabla AND m.referenciaId = :id")
    List<MovimientoStock> buscarPorReferencia(@Param("tabla") String tabla, @Param("id") Long id);
    
    @Query("SELECT COUNT(m) FROM MovimientoStock m WHERE m.tipoMovimiento = :tipo AND m.fechaMovimiento >= :fecha")
    long contarPorTipoYFechas(@Param("tipo") TipoMovimiento tipo, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT m FROM MovimientoStock m WHERE m.lote.medicamento.id = :medicamentoId ORDER BY m.fechaMovimiento DESC")
    List<MovimientoStock> buscarPorMedicamento(@Param("medicamentoId") Long medicamentoId);
}