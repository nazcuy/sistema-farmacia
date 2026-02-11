package com.farmaciasalud.modulos.ventas_dispensacion.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Entrega;
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Receta;
import com.farmaciasalud.modulos.ventas_dispensacion.dto.DispensacionDTO;
import com.farmaciasalud.modulos.ventas_dispensacion.servicio.DispensacionServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispensaciones")
@RequiredArgsConstructor

public class DispensacionControlador {
    
    private final DispensacionServicio dispensacionServicio;
    
    /**
     * POST /api/dispensaciones
     * Realiza una dispensación de medicamentos.
     */
    @PostMapping
    public ResponseEntity<Entrega> dispensar(@RequestBody DispensacionDTO dto) {
        try {
            Entrega entrega = dispensacionServicio.dispensar(dto, dto.getUsuarioEntregaId());
            return ResponseEntity.status(HttpStatus.CREATED).body(entrega);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    /**
     * GET /api/dispensaciones/persona/{id}
     * Obtiene el historial de dispensaciones de una persona.
     */
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<Entrega>> historialPersona(@PathVariable Long personaId) {
        return ResponseEntity.ok(dispensacionServicio.historialDispensaciones(personaId));
    }
    
    /**
     * GET /api/dispensaciones/hoy
     * Lista todas las dispensaciones del día actual.
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<Entrega>> dispensacionesDelDia() {
        return ResponseEntity.ok(dispensacionServicio.entregasDelDia());
    }
}