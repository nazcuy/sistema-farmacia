package com.farmaciasalud.modulos.pacientes.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepositorio extends JpaRepository<Persona, Long>{
    Optional<Persona> findByNumeroDocumento(String numeroDocumento);
    
    List<Persona> findByApellidoContainingIgnoreCase(String apellido);
    
    List<Persona> findByNombreContainingIgnoreCase(String nombre);
    
    List<Persona> findByActivoTrue();
    
    List<Persona> findByActivoTrueOrderByApellidoAsc();
    
    @Query("SELECT p FROM Persona p WHERE p.nombre LIKE %:nombre% OR p.apellido LIKE %:apellido%")
    List<Persona> buscarPorNombreOApellido(@Param("nombre") String nombre, @Param("apellido") String apellido);
    
    @Query("SELECT p FROM Persona p WHERE p.fechaNacimiento BETWEEN :inicio AND :fin")
    List<Persona> buscarPorRangoFechasNacimiento(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    boolean existsByNumeroDocumento(String numeroDocumento);
    
    @Query("SELECT COUNT(p) FROM Persona p WHERE p.activo = true")
    long contarPersonasActivas();
    
    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.domicilios WHERE p.id = :id")
    Optional<Persona> buscarConDomicilios(@Param("id") Long id);
}
