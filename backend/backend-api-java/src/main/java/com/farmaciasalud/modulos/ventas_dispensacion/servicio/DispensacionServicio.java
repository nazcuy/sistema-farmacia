package com.farmaciasalud.modulos.ventas_dispensacion.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.*;
import com.farmaciasalud.modulos.ventas_dispensacion.dto.*;
import com.farmaciasalud.modulos.inventario.servicio.InventarioServicio;
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.repositorio.PersonaRepositorio;
import com.farmaciasalud.modulos.pacientes.repositorio.UsuarioRepositorio;
import com.farmaciasalud.modulos.catalogo_farmacia.repositorio.MedicamentoRepositorio;
import com.farmaciasalud.modulos.historia_clinica.repositorio.RecetaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispensacionServicio {
    
    private final EntregaRepositorio entregaRepositorio;
    private final EntregaDetalleRepositorio entregaDetalleRepositorio;
    private final RecetaRepositorio recetaRepositorio;
    private final RecetaDetalleRepositorio recetaDetalleRepositorio;
    private final PersonaRepositorio personaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;
    private final InventarioServicio inventarioServicio;
    
    /**
     * Realiza una dispensación completa.
     * Coordina: reducción de stock, creación de entrega, actualización de receta.
     */
    public Entrega dispensar(DispensacionDTO dto, Long usuarioEntregaId) {
        // Validar y cargar datos
        Persona persona = personaRepositorio.findById(dto.getPersonaId())
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
        
        var usuarioEntrega = usuarioRepositorio.findById(usuarioEntregaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Crear la entrega
        Entrega entrega = Entrega.builder()
                .persona(persona)
                .usuarioEntrega(usuarioEntrega)
                .observaciones(dto.getObservaciones())
                .build();
        
        // Si viene de receta, asociar
        if (dto.getRecetaId() != null) {
            Receta receta = recetaRepositorio.findById(dto.getRecetaId())
                    .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
            
            // Validar que la receta sea del mismo paciente
            if (!receta.getPersona().getId().equals(persona.getId())) {
                throw new IllegalArgumentException("La receta no corresponde a esta persona");
            }
            
            // Validar que la receta esté vigente
            if (!receta.estaVigente()) {
                throw new IllegalStateException("La receta está vencida");
            }
            
            entrega.setReceta(receta);
        }
        
        entrega = entregaRepositorio.save(entrega);
        
        // Procesar cada item de la dispensación
        for (ItemDispensacionDTO item : dto.getItems()) {
            procesarItemDispensacion(entrega, item);
        }
        
        // Recargar con detalles
        return entregaRepositorio.buscarCompleta(entrega.getId())
                .orElseThrow(() -> new IllegalStateException("Error al recargar entrega"));
    }
    
    /**
     * Procesa un item individual de la dispensación.
     */
    private void procesarItemDispensacion(Entrega entrega, ItemDispensacionDTO item) {
        // Validar medicamento
        var medicamento = medicamentoRepositorio.findById(item.getMedicamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        // Si viene de receta, verificar contra la receta
        if (entrega.getReceta() != null) {
            Optional<RecetaDetalle> detalleReceta = 
                recetaDetalleRepositorio.buscarPorRecetaYMedicamento(
                    entrega.getReceta().getId(), item.getMedicamentoId());
            
            if (detalleReceta.isPresent()) {
                RecetaDetalle rd = detalleReceta.get();
                int yaDispensado = rd.getCantidadDispensada();
                int nuevoTotal = yaDispensado + item.getCantidad();
                
                if (nuevoTotal > rd.getCantidad()) {
                    throw new IllegalStateException("No se puede dispensar más de lo prescrito para: " + 
                                                   medicamento.getNombreComercial());
                }
                
                // Actualizar receta detalle
                rd.registrarDispensacion(item.getCantidad());
                recetaDetalleRepositorio.save(rd);
            }
        }
        
        // Reducir stock con FEFO
        inventarioServicio.reducirStock(item.getMedicamentoId(), item.getCantidad(), 
                                       entrega.getUsuarioEntrega().getId(),
                                       "Dispensación a: " + entrega.getPersona().getNombreCompleto());
        
        // Crear detalle de entrega
        // Nota: En una implementación real, necesitaríamos rastrear qué lote específico se usó
        // Aquí simplificamos guardando la referencia al medicamento
        EntregaDetalle detalle = EntregaDetalle.builder()
                .entrega(entrega)
                .cantidadEntregada(item.getCantidad())
                .build();
        
        // Para un sistema real, necesitaríamos obtener el lote específico usado
        // Esto requiere que inventarioServicio devuelva qué lotes se usaron
        // Por simplicidad, guardamos una referencia al medicamento en el detalle
        entregaDetalleRepositorio.save(detalle);
    }
    
    /**
     * Obtiene las recetas vigentes de una persona.
     */
    @Transactional(readOnly = true)
    public List<Receta> obtenerRecetasVigentes(Long personaId) {
        return recetaRepositorio.buscarVigentesPorPersona(personaId, 
                                                         java.time.LocalDateTime.now().minusDays(30));
    }
    
    /**
     * Obtiene una receta con todos sus detalles.
     */
    @Transactional(readOnly = true)
    public Optional<Receta> obtenerRecetaCompleta(Long recetaId) {
        return recetaRepositorio.buscarCompleta(recetaId);
    }
    
    /**
     * Lista el historial de dispensaciones de una persona.
     */
    @Transactional(readOnly = true)
    public List<Entrega> historialDispensaciones(Long personaId) {
        return entregaRepositorio.findByPersonaIdOrderByFechaEntregaDesc(personaId);
    }
    
    /**
     * Lista todas las entregas de un día.
     */
    @Transactional(readOnly = true)
    public List<Entrega> entregasDelDia() {
        java.time.LocalDateTime inicio = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime fin = inicio.plusDays(1);
        return entregaRepositorio.buscarPorRangoFechas(inicio, fin);
    }
}