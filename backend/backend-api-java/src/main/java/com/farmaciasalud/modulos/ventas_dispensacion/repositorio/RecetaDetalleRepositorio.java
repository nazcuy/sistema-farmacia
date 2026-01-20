package com.farmaciasalud.modulos.ventas_dispensacion.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.RecetaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaDetalleRepositorio extends JpaRepository<RecetaDetalle, Long> {
    
    List<RecetaDetalle> findByRecetaId(Long recetaId);
    
    @Query("SELECT rd FROM RecetaDetalle rd WHERE rd.receta.id = :recetaId AND rd.cantidadDispensada < rd.cantidad")
    List<RecetaDetalle> findPendientesDeDispensar(@Param("recetaId") Long recetaId);
    
    @Query("SELECT rd FROM RecetaDetalle rd LEFT JOIN FETCH rd.medicamento WHERE rd.id = :id")
    Optional<RecetaDetalle> buscarConMedicamento(@Param("id") Long id);
    
    @Query("SELECT SUM(rd.cantidad) FROM RecetaDetalle rd WHERE rd.receta.id = :recetaId")
    Integer calcularTotalItems(@Param("recetaId") Long recetaId);
    
    @Query("SELECT rd FROM RecetaDetalle rd WHERE rd.receta.id = :recetaId AND rd.medicamento.id = :medicamentoId")
    Optional<RecetaDetalle> buscarPorRecetaYMedicamento(@Param("recetaId") Long recetaId, 
                                                         @Param("medicamentoId") Long medicamentoId);
}