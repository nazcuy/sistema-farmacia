package com.farmaciasalud.modulos.pacientes.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.Domicilio;
import com.farmaciasalud.modulos.pacientes.dominio.TipoDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DomicilioRepositorio extends JpaRepository<Domicilio, Long> {
    
    List<Domicilio> findByPersonaId(Long personaId);
    
    List<Domicilio> findByPersonaIdAndActivoTrue(Long personaId);
    
    List<Domicilio> findByBarrio(String barrio);
    
    List<Domicilio> findByLocalidad(String localidad);
    
    @Query("SELECT d FROM Domicilio d WHERE d.persona.id = :personaId AND d.tipo = :tipo")
    List<Domicilio> buscarPorPersonaYTipo(@Param("personaId") Long personaId, @Param("tipo") TipoDomicilio tipo);
    
    @Query("SELECT d FROM Domicilio d WHERE d.barrio = :barrio AND d.localidad = :localidad")
    List<Domicilio> buscarPorBarrioYLocalidad(@Param("barrio") String barrio, @Param("localidad") String localidad);
    
    @Query("SELECT COUNT(d) FROM Domicilio d WHERE d.persona.id = :personaId")
    long contarDomiciliosDePersona(@Param("personaId") Long personaId);
}