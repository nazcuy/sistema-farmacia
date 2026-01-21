package com.farmaciasalud.modulos.historia_clinica.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.historia_clinica.dominio.HistoriaClinica;
import com.farmaciasalud.modulos.historia_clinica.dto.HistoriaClinicaDTO;
import com.farmaciasalud.modulos.historia_clinica.repositorio.HistoriaClinicaRepositorio;
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.repositorio.PersonaRepositorio;
import com.farmaciasalud.modulos.pacientes.repositorio.UsuarioRepositorio;
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Receta;
import com.farmaciasalud.modulos.ventas_dispensacion.repositorio.RecetaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoriaClinicaServicio {
    
    private final HistoriaClinicaRepositorio historiaRepositorio;
    private final RecetaRepositorio recetaRepositorio;
    private final PersonaRepositorio personaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    
    /**
     * Lista todas las historias de un paciente ordenadas por fecha.
     */
    @Transactional(readOnly = true)
    public List<HistoriaClinica> listarPorPaciente(Long personaId) {
        return historiaRepositorio.findByPersonaIdOrderByFechaConsultaDesc(personaId);
    }
    
    /**
     * Obtiene la última historia clínica de un paciente.
     */
    @Transactional(readOnly = true)
    public Optional<HistoriaClinica> obtenerUltima(Long personaId) {
        return Optional.ofNullable(historiaRepositorio.buscarUltimaHistoria(personaId));
    }
    
    /**
     * Obtiene una historia clínica con sus recetas.
     */
    @Transactional(readOnly = true)
    public Optional<HistoriaClinica> obtenerConRecetas(Long historiaId) {
        return historiaRepositorio.buscarConRecetas(historiaId);
    }
    
    /**
     * Registra una nueva consulta médica con su receta.
     */
    public HistoriaClinica registrarConsulta(HistoriaClinicaDTO dto, Long medicoId) {
        // Validar que existan paciente y médico
        Persona paciente = personaRepositorio.findById(dto.getPersonaId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        
        var medico = usuarioRepositorio.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));
        
        // Validar que el usuario sea médico
        if (!medico.esMedico()) {
            throw new IllegalStateException("Solo un médico puede crear historias clínicas");
        }
        
        // Crear la historia clínica
        HistoriaClinica historia = HistoriaClinica.builder()
                .persona(paciente)
                .medico(medico)
                .motivoConsulta(dto.getMotivoConsulta())
                .diagnostico(dto.getDiagnostico())
                .observaciones(dto.getObservaciones())
                .build();
        
        historia = historiaRepositorio.save(historia);
        
        // Si hay recetas en el DTO, crearlas
        if (dto.getRecetas() != null && !dto.getRecetas().isEmpty()) {
            for (var recetaDTO : dto.getRecetas()) {
                Receta receta = crearReceta(recetaDTO, historia, medico, paciente);
                historia.agregarReceta(receta);
            }
        }
        
        return historiaRepositorio.save(historia);
    }
    
    /**
     * Crea una receta asociada a una historia clínica.
     */
    private Receta crearReceta(RecetaDTO dto, HistoriaClinica historia, 
                               com.farmaciasalud.modulos.pacientes.dominio.Usuario medico,
                               Persona paciente) {
        Receta receta = Receta.builder()
                .historiaClinica(historia)
                .medico(medico)
                .persona(paciente)
                .observaciones(dto.getObservaciones())
                .vigenciaDias(dto.getVigenciaDias() != null ? dto.getVigenciaDias() : 30)
                .build();
        
        // Agregar detalles de la receta
        if (dto.getDetalles() != null) {
            for (var detalleDTO : dto.getDetalles()) {
                var medicamento = 
                    com.farmaciasalud.modulos.catalogo_farmacia.repositorio.MedicamentoRepositorio
                        .findById(detalleDTO.getMedicamentoId())
                        .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
                
                RecetaDetalle detalle = RecetaDetalle.builder()
                        .medicamento(medicamento)
                        .cantidad(detalleDTO.getCantidad())
                        .dosificacion(detalleDTO.getDosificacion())
                        .build();
                
                receta.agregarDetalle(detalle);
            }
        }
        
        return recetaRepositorio.save(receta);
    }
    
    /**
     * Busca historias por rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<HistoriaClinica> buscarPorFechas(Long personaId, LocalDateTime inicio, LocalDateTime fin) {
        return historiaRepositorio.buscarPorPersonaYFechas(personaId, inicio, fin);
    }
}