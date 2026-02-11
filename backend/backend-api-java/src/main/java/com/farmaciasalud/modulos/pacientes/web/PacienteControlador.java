package com.farmaciasalud.modulos.pacientes.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.dto.PersonaRegistroDTO;
import com.farmaciasalud.modulos.pacientes.dto.PersonaActualizacionDTO;
import com.farmaciasalud.modulos.pacientes.servicio.PacienteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor

public class PacienteControlador {
    private final PacienteServicio pacienteServicio;
    
    /**
     * GET /api/pacientes
     * Lista todas las personas activas.
     */
    @GetMapping
    public ResponseEntity<List<Persona>> listarTodos() {
        List<Persona> personas = pacienteServicio.listarTodos();
        return ResponseEntity.ok(personas);
    }
    
    /**
     * GET /api/pacientes/{id}
     * Busca una persona por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Persona> buscarPorId(@PathVariable Long id) {
        return pacienteServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/pacientes/documento/{documento}
     * Busca una persona por número de documento.
     * Útil para buscar pacientes rápidamente en mostrador.
     */
    @GetMapping("/documento/{documento}")
    public ResponseEntity<Persona> buscarPorDocumento(@PathVariable String documento) {
        return pacienteServicio.buscarPorDocumento(documento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/pacientes/buscar?apellido=garcia
     * Busca personas por apellido (búsqueda parcial).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Persona>> buscarPorApellido(@RequestParam String apellido) {
        List<Persona> personas = pacienteServicio.buscarPorApellido(apellido);
        return ResponseEntity.ok(personas);
    }
    
    /**
     * GET /api/pacientes/buscar-general?termino=john
     * Búsqueda general por nombre o apellido.
     */
    @GetMapping("/buscar-general")
    public ResponseEntity<List<Persona>> buscarGeneral(@RequestParam String termino) {
        List<Persona> personas = pacienteServicio.buscarPorNombreOApellido(termino);
        return ResponseEntity.ok(personas);
    }
    
    /**
     * POST /api/pacientes
     * Registra una nueva persona en el sistema.
     */
    @PostMapping
    public ResponseEntity<Persona> registrar(@RequestBody PersonaRegistroDTO dto) {
        try {
            Persona persona = pacienteServicio.registrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(persona);
        } catch (IllegalArgumentException e) {
            // Retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PUT /api/pacientes/{id}
     * Actualiza los datos de una persona existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizar(@PathVariable Long id, 
                                              @RequestBody PersonaActualizacionDTO dto) {
        try {
            Persona persona = pacienteServicio.actualizar(id, dto);
            return ResponseEntity.ok(persona);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/pacientes/{id}
     * Desactiva una persona (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            pacienteServicio.eliminar(id);
            return ResponseEntity.noContent().build();  // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/pacientes/{id}/domicilios
     * Obtiene una persona con sus domicilios.
     */
    @GetMapping("/{id}/domicilios")
    public ResponseEntity<Persona> obtenerConDomicilios(@PathVariable Long id) {
        return pacienteServicio.buscarConDomicilios(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}