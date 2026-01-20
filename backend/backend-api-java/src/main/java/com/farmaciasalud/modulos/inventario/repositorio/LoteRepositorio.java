package com.farmaciasalud.modulos.inventario.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.inventario.dominio.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepositorio extends JpaRepository<Lote, Long> {
    
    List<Lote> findByMedicamentoId(Long medicamentoId);
    
    List<Lote> findByMedicamentoIdAndCantidadActualGreaterThan(Long medicamentoId, Integer cantidad);
    
    @Query("SELECT l FROM Lote l WHERE l.medicamento.id = :medicamentoId AND l.cantidadActual > 0 ORDER BY l.fechaVencimiento ASC")
    List<Lote> buscarLotesConStockOrdenadosPorVencimiento(@Param("medicamentoId") Long medicamentoId);
    
    List<Lote> findByNumeroLote(String numeroLote);
    
    @Query("SELECT l FROM Lote l WHERE l.fechaVencimiento BETWEEN :inicio AND :fin")
    List<Lote> buscarPorRangoVencimiento(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT l FROM Lote l WHERE l.fechaVencimiento < :fecha AND l.cantidadActual > 0")
    List<Lote> buscarVencidos(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT l FROM Lote l WHERE l.fechaVencimiento BETWEEN :fecha AND :fechaMas AND l.cantidadActual > 0")
    List<Lote> buscarPorVencer(@Param("fecha") LocalDate fecha, @Param("fechaMas") LocalDate fechaMas);
    
    @Query("SELECT SUM(l.cantidadActual) FROM Lote l WHERE l.medicamento.id = :medicamentoId")
    Integer calcularStockTotal(@Param("medicamentoId") Long medicamentoId);
    
    @Query("SELECT l FROM Lote l JOIN FETCH l.medicamento WHERE l.id = :id")
    Lote buscarConMedicamento(@Param("id") Long id);
    
    @Query("SELECT COUNT(l) FROM Lote l WHERE l.cantidadActual <= 0")
    long contarLotesVacios();
    
    @Query("SELECT l FROM Lote l WHERE l.medicamento.id = :medicamentoId AND l.id = :loteId")
    Optional<Lote> buscarPorMedicamentoYLote(@Param("medicamentoId") Long medicamentoId, @Param("loteId") Long loteId);
}