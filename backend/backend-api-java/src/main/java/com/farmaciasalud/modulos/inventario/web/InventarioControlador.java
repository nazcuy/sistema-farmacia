package com.farmaciasalud.modulos.inventario.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */

import com.farmaciasalud.modulos.inventario.dominio.Lote;
import com.farmaciasalud.modulos.inventario.dominio.MovimientoStock;
import com.farmaciasalud.modulos.inventario.dto.LoteIngresoDTO;
import com.farmaciasalud.modulos.inventario.dto.AjusteStockDTO;
import com.farmaciasalud.modulos.inventario.servicio.InventarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor

public class InventarioControlador {
    
    private final InventarioServicio inventarioServicio;
    
    /**
     * GET /api/inventario/medicamento/{id}/lotes
     * Lista los lotes con stock de un medicamento.
     */
    @GetMapping("/medicamento/{medicamentoId}/lotes")
    public ResponseEntity<List<Lote>> listarLotesMedicamento(@PathVariable Long medicamentoId) {
        return ResponseEntity.ok(inventarioServicio.listarLotesConStock(medicamentoId));
    }
    
    /**
     * GET /api/inventario/medicamento/{id}/stock
     * Obtiene el stock total de un medicamento.
     */
    @GetMapping("/medicamento/{id}/stock")
    public ResponseEntity<Integer> obtenerStock(@PathVariable Long id) {
        Integer stock = inventarioServicio.obtenerStockTotal(id);
        return ResponseEntity.ok(stock);
    }
    
    /**
     * GET /api/inventario/proximos-vencer?dias=30
     * Lista lotes próximos a vencer.
     */
    @GetMapping("/proximos-vencer")
    public ResponseEntity<List<Lote>> listarProximosAVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(inventarioServicio.listarProximosAVencer(dias));
    }
    
    /**
     * GET /api/inventario/vencidos
     * Lista lotes ya vencidos.
     */
    @GetMapping("/vencidos")
    public ResponseEntity<List<Lote>> listarVencidos() {
        return ResponseEntity.ok(inventarioServicio.listarVencidos());
    }
    
    /**
     * GET /api/inventario/lote/{id}/historial
     * Obtiene el historial de movimientos de un lote.
     */
    @GetMapping("/lote/{id}/historial")
    public ResponseEntity<List<MovimientoStock>> obtenerHistorialLote(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioServicio.obtenerHistorialLote(id));
    }
    
    /**
     * GET /api/inventario/lote/{id}
     * Obtiene un lote con su medicamento.
     */
    @GetMapping("/lote/{id}")
    public ResponseEntity<Lote> obtenerLote(@PathVariable Long id) {
        try {
            Lote lote = inventarioServicio.obtenerLoteConMedicamento(id);
            return ResponseEntity.ok(lote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/inventario/lotes
     * Ingresa un nuevo lote al inventario.
     */
    @PostMapping("/lotes")
    public ResponseEntity<Lote> ingresarLote(@RequestBody LoteIngresoDTO dto) {
        try {
            Lote lote = inventarioServicio.ingresarLote(dto, dto.getUsuarioId());
            return ResponseEntity.status(HttpStatus.CREATED).body(lote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * POST /api/inventario/ajuste-positivo
     * Aumenta stock de un lote.
     */
    @PostMapping("/ajuste-positivo")
    public ResponseEntity<Void> ajustePositivo(@RequestBody AjusteStockDTO dto) {
        try {
            inventarioServicio.aumentarStock(dto.getLoteId(), dto.getCantidad(), 
                                            dto.getMotivo(), dto.getUsuarioId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/inventario/ajuste-negativo
     * Reduce stock de un lote (faltante, daño, etc.).
     */
    @PostMapping("/ajuste-negativo")
    public ResponseEntity<Void> ajusteNegativo(@RequestBody AjusteStockDTO dto) {
        try {
            inventarioServicio.ajustarNegativo(dto.getLoteId(), dto.getCantidad(), 
                                               dto.getMotivo(), dto.getUsuarioId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * POST /api/inventario/lote/{id}/vencimiento
     * Registra el vencimiento de un lote.
     */
    @PostMapping("/lote/{id}/vencimiento")
    public ResponseEntity<Void> registrarVencimiento(@PathVariable Long id,
                                                     @RequestParam(required = false) String observaciones,
                                                     @RequestParam Long usuarioId) {
        try {
            inventarioServicio.registrarVencimiento(id, observaciones, usuarioId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}