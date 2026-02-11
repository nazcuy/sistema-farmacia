package com.farmaciasalud.modulos.ventas_dispensacion.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Receta;
import com.farmaciasalud.modulos.ventas_dispensacion.servicio.DispensacionServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor

public class RecetaControlador {
    
    private final DispensacionServicio dispensacionServicio;
    
    /**
     * GET /api/recetas/persona/{personaId}/vigentes
     * Obtiene las recetas vigentes de una persona.
     */
    @GetMapping("/persona/{personaId}/vigentes")
    public ResponseEntity<List<Receta>> obtenerRecetasVigentes(@PathVariable Long personaId) {
        return ResponseEntity.ok(dispensacionServicio.obtenerRecetasVigentes(personaId));
    }
    
    /**
     * GET /api/recetas/{id}
     * Obtiene una receta completa con todos sus detalles.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerReceta(@PathVariable Long id) {
        return dispensacionServicio.obtenerRecetaCompleta(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}