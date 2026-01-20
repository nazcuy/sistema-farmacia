package com.farmaciasalud.modulos.historia_clinica.repositorio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.historia_clinica.dominio.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoriaClinicaRepositorio extends JpaRepository<HistoriaClinica, Long> {
    
    List<HistoriaClinica> findByPersonaIdOrderByFechaConsultaDesc(Long personaId);
    
    List<HistoriaClinica> findByMedicoId(Long medicoId);
    
    @Query("SELECT h FROM HistoriaClinica h WHERE h.medico.id = :medicoId ORDER BY h.fechaConsulta DESC")
    List<HistoriaClinica> buscarPorMedicoOrdenado(@Param("medicoId") Long medicoId);
    
    @Query("SELECT h FROM HistoriaClinica h WHERE h.persona.id = :personaId AND h.fechaConsulta BETWEEN :inicio AND :fin")
    List<HistoriaClinica> buscarPorPersonaYFechas(@Param("personaId") Long personaId, 
                                                   @Param("inicio") LocalDateTime inicio, 
                                                   @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(h) FROM HistoriaClinica h WHERE h.medico.id = :medicoId AND h.fechaConsulta >= :fecha")
    long contarConsultasMedico(@Param("medicoId") Long medicoId, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT h FROM HistoriaClinica h LEFT JOIN FETCH h.recetas WHERE h.id = :id")
    HistoriaClinica buscarConRecetas(@Param("id") Long id);
    
    @Query("SELECT h FROM HistoriaClinica h WHERE h.persona.id = :personaId ORDER BY h.fechaConsulta DESC LIMIT 1")
    HistoriaClinica buscarUltimaHistoria(@Param("personaId") Long personaId);
}