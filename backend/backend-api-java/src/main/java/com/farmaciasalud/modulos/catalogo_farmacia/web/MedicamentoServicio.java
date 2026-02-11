package com.farmaciasalud.modulos.catalogo_farmacia.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.catalogo_farmacia.dominio.FormaFarmaceutica;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import com.farmaciasalud.modulos.catalogo_farmacia.dto.MedicamentoDTO;
import com.farmaciasalud.modulos.catalogo_farmacia.servicio.MedicamentoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor

public class MedicamentoServicio {
    private final MedicamentoServicio medicamentoServicio;
    
    /**
     * GET /api/medicamentos
     * Lista todos los medicamentos activos.
     */
    @GetMapping
    public ResponseEntity<List<Medicamento>> listarTodos() {
        return ResponseEntity.ok(medicamentoServicio.listarTodos());
    }
    
    /**
     * GET /api/medicamentos/{id}
     * Busca un medicamento por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable Long id) {
        return medicamentoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/medicamentos/codigo-barras/{codigo}
     * Busca por código de barras (principal método de búsqueda en punto de venta).
     */
    @GetMapping("/codigo-barras/{codigo}")
    public ResponseEntity<Medicamento> buscarPorCodigoBarras(@PathVariable String codigo) {
        return medicamentoServicio.buscarPorCodigoBarras(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/medicamentos/buscar?termino=para
     * Búsqueda general por texto.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Medicamento>> buscar(@RequestParam String termino) {
        List<Medicamento> medicamentos = medicamentoServicio.buscar(termino);
        return ResponseEntity.ok(medicamentos);
    }
    
    /**
     * GET /api/medicamentos/forma?forma=COMPRIMIDO
     * Filtra por forma farmacéutica.
     */
    @GetMapping("/forma")
    public ResponseEntity<List<Medicamento>> buscarPorForma(@RequestParam FormaFarmaceutica forma) {
        return ResponseEntity.ok(medicamentoServicio.buscarPorForma(forma));
    }
    
    /**
     * GET /api/medicamentos/venta-libre
     * Lista medicamentos que no requieren receta.
     */
    @GetMapping("/venta-libre")
    public ResponseEntity<List<Medicamento>> listarVentaLibre() {
        return ResponseEntity.ok(medicamentoServicio.listarVentaLibre());
    }
    
    /**
     * GET /api/medicamentos/{id}/stock
     * Obtiene un medicamento con información de stock.
     */
    @GetMapping("/{id}/stock")
    public ResponseEntity<Medicamento> obtenerConStock(@PathVariable Long id) {
        return medicamentoServicio.buscarConStock(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/medicamentos/laboratorios
     * Lista todos los laboratorios disponibles.
     */
    @GetMapping("/laboratorios")
    public ResponseEntity<List<String>> listarLaboratorios() {
        return ResponseEntity.ok(medicamentoServicio.listarLaboratorios());
    }
    
    /**
     * POST /api/medicamentos
     * Registra un nuevo medicamento.
     */
    @PostMapping
    public ResponseEntity<Medicamento> registrar(@RequestBody MedicamentoDTO dto) {
        try {
            Medicamento medicamento = medicamentoServicio.registrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(medicamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PUT /api/medicamentos/{id}
     * Actualiza un medicamento.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> actualizar(@PathVariable Long id, 
                                                  @RequestBody MedicamentoDTO dto) {
        try {
            Medicamento medicamento = medicamentoServicio.actualizar(id, dto);
            return ResponseEntity.ok(medicamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/medicamentos/{id}
     * Desactiva un medicamento.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            medicamentoServicio.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // No se puede eliminar porque tiene stock
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}