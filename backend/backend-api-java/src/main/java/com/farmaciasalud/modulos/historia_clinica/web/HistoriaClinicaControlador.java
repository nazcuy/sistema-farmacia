package com.farmaciasalud.modulos.historia_clinica.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.historia_clinica.dominio.HistoriaClinica;
import com.farmaciasalud.modulos.historia_clinica.dto.HistoriaClinicaDTO;
import com.farmaciasalud.modulos.historia_clinica.servicio.HistoriaClinicaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historias-clinicas")
@RequiredArgsConstructor

public class HistoriaClinicaControlador {
    
    private final HistoriaClinicaServicio historiaClinicaServicio;
    
    /**
     * GET /api/historias-clinicas/persona/{personaId}
     * Lista todas las historias de un paciente.
     */
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<HistoriaClinica>> listarPorPaciente(@PathVariable Long personaId) {
        return ResponseEntity.ok(historiaClinicaServicio.listarPorPaciente(personaId));
    }
    
    /**
     * GET /api/historias-clinicas/persona/{personaId}/ultima
     * Obtiene la última historia clínica de un paciente.
     */
    @GetMapping("/persona/{personaId}/ultima")
    public ResponseEntity<HistoriaClinica> obtenerUltima(@PathVariable Long personaId) {
        return historiaClinicaServicio.obtenerUltima(personaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/historias-clinicas/{id}
     * Obtiene una historia clínica con sus recetas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoriaClinica> obtener(@PathVariable Long id) {
        return historiaClinicaServicio.obtenerConRecetas(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/historias-clinicas
     * Registra una nueva consulta médica con sus recetas.
     * Requiere que el usuario sea médico.
     */
    @PostMapping
    public ResponseEntity<HistoriaClinica> registrar(@RequestBody HistoriaClinicaDTO dto,
                                                     @RequestHeader("X-Usuario-Id") Long medicoId) {
        try {
            HistoriaClinica historia = historiaClinicaServicio.registrarConsulta(dto, medicoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(historia);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}