package com.farmaciasalud.modulos.catalogo_farmacia.repositorio;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.FormaFarmaceutica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepositorio extends JpaRepository<Medicamento, Long> {
    
    Optional<Medicamento> findByCodigoBarras(String codigoBarras);
    
    Optional<Medicamento> findByCodigoBarrasAndActivoTrue(String codigoBarras);
    
    List<Medicamento> findByNombreComercialContainingIgnoreCase(String nombre);
    
    List<Medicamento> findByNombreGenericoContainingIgnoreCase(String nombre);
    
    List<Medicamento> findByLaboratorioContainingIgnoreCase(String laboratorio);
    
    List<Medicamento> findByFormaFarmaceutica(FormaFarmaceutica forma);
    
    List<Medicamento> findByRequiereRecetaFalse();
    
    List<Medicamento> findByActivoTrue();
    
    List<Medicamento> findByActivoTrueOrderByNombreComercialAsc();
    
    @Query("SELECT m FROM Medicamento m WHERE m.activo = true AND " +
           "(LOWER(m.nombreComercial) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(m.nombreGenerico) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "m.codigoBarras LIKE CONCAT('%', :busqueda, '%'))")
    List<Medicamento> buscarPorTexto(@Param("busqueda") String busqueda);
    
    @Query("SELECT m FROM Medicamento m WHERE m.formaFarmaceutica = :forma AND m.activo = true")
    List<Medicamento> buscarPorFormaYFiltro(@Param("forma") FormaFarmaceutica forma);
    
    @Query("SELECT COUNT(m) FROM Medicamento m WHERE m.activo = true")
    long contarMedicamentosActivos();
    
    @Query("SELECT DISTINCT m.laboratorio FROM Medicamento m WHERE m.laboratorio IS NOT NULL ORDER BY m.laboratorio")
    List<String> listarLaboratorios();
    
    boolean existsByCodigoBarras(String codigoBarras);
    
    @Query("SELECT m FROM Medicamento m LEFT JOIN FETCH m.lotes WHERE m.id = :id")
    Optional<Medicamento> buscarConLotes(@Param("id") Long id);
}
