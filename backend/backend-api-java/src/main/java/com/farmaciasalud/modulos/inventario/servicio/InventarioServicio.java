package com.farmaciasalud.modulos.inventario.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.inventario.dominio.Lote;
import com.farmaciasalud.modulos.inventario.dominio.MovimientoStock;
import com.farmaciasalud.modulos.inventario.dominio.TipoMovimiento;
import com.farmaciasalud.modulos.inventario.dto.LoteIngresoDTO;
import com.farmaciasalud.modulos.inventario.dto.AjusteStockDTO;
import com.farmaciasalud.modulos.inventario.repositorio.LoteRepositorio;
import com.farmaciasalud.modulos.inventario.repositorio.MovimientoStockRepositorio;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import com.farmaciasalud.modulos.catalogo_farmacia.repositorio.MedicamentoRepositorio;
import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import com.farmaciasalud.modulos.pacientes.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServicio {
    
    private final LoteRepositorio loteRepositorio;
    private final MovimientoStockRepositorio movimientoRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    
    /**
     * Lista todos los lotes con stock de un medicamento.
     * Ordenados por fecha de vencimiento (FEFO).
     */
    @Transactional(readOnly = true)
    public List<Lote> listarLotesConStock(Long medicamentoId) {
        return loteRepositorio.buscarLotesConStockOrdenadosPorVencimiento(medicamentoId);
    }
    
    /**
     * Obtiene el stock total de un medicamento.
     * Suma las cantidades de todos los lotes.
     */
    @Transactional(readOnly = true)
    public Integer obtenerStockTotal(Long medicamentoId) {
        Integer stock = loteRepositorio.calcularStockTotal(medicamentoId);
        return stock != null ? stock : 0;
    }
    
    /**
     * Ingresa un nuevo lote al inventario.
     * Crea el lote y registra el movimiento de ingreso.
     */
    public Lote ingresarLote(LoteIngresoDTO dto, Long usuarioId) {
        // Validar que el medicamento exista
        Medicamento medicamento = medicamentoRepositorio.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        // Crear el lote
        Lote lote = Lote.builder()
                .medicamento(medicamento)
                .numeroLote(dto.getNumeroLote())
                .fechaVencimiento(dto.getFechaVencimiento())
                .cantidadInicial(dto.getCantidad())
                .cantidadActual(dto.getCantidad())
                .precioCompra(dto.getPrecioCompra())
                .build();
        
        // Guardar primero para obtener el ID
        lote = loteRepositorio.save(lote);
        
        // Registrar movimiento de stock
        registrarMovimiento(lote, TipoMovimiento.INGRESO, dto.getCantidad(), 
                           "Ingreso por compra", usuarioId);
        
        return lote;
    }
    
    /**
     * Reduce stock de un lote para dispensación.
     * Usa el método FEFO para elegir lotes.
     */
    public void reducirStock(Long medicamentoId, int cantidad, Long usuarioId, String referencia) {
        List<Lote> lotes = loteRepositorio.buscarLotesConStockOrdenadosPorVencimiento(medicamentoId);
        
        int cantidadAReducir = cantidad;
        
        for (Lote lote : lotes) {
            if (cantidadAReducir <= 0) break;
            
            int cantidadDelLote = Math.min(cantidadAReducir, lote.getCantidadActual());
            
            // Reducir stock del lote
            lote.reducirStock(cantidadDelLote);
            loteRepositorio.save(lote);
            
            // Registrar movimiento
            registrarMovimiento(lote, TipoMovimiento.EGRESO, cantidadDelLote, 
                               referencia, usuarioId);
            
            cantidadAReducir -= cantidadDelLote;
        }
        
        if (cantidadAReducir > 0) {
            throw new IllegalStateException("Stock insuficiente. Solo se pudo reducir " + 
                                           (cantidad - cantidadAReducir) + " de " + cantidad);
        }
    }
    
    /**
     * Aumenta stock de un lote (devolución o ajuste positivo).
     */
    public void aumentarStock(Long loteId, int cantidad, String motivo, Long usuarioId) {
        Lote lote = loteRepositorio.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        lote.aumentarStock(cantidad);
        loteRepositorio.save(lote);
        
        registrarMovimiento(lote, TipoMovimiento.AJUSTE_POSITIVO, cantidad, 
                           motivo, usuarioId);
    }
    
    /**
     * Hace un ajuste negativo de stock (faltante, daño, robo).
     */
    public void ajustarNegativo(Long loteId, int cantidad, String motivo, Long usuarioId) {
        Lote lote = loteRepositorio.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        if (cantidad > lote.getCantidadActual()) {
            throw new IllegalStateException("No se puede ajustar más del stock disponible");
        }
        
        lote.reducirStock(cantidad);
        loteRepositorio.save(lote);
        
        registrarMovimiento(lote, TipoMovimiento.AJUSTE_NEGATIVO, cantidad, 
                           motivo, usuarioId);
    }
    
    /**
     * Registra el vencimiento de un lote.
     */
    public void registrarVencimiento(Long loteId, String observaciones, Long usuarioId) {
        Lote lote = loteRepositorio.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        int cantidadVencida = lote.getCantidadActual();
        
        // Reducir a cero
        lote.setCantidadActual(0);
        loteRepositorio.save(lote);
        
        // Registrar movimiento
        registrarMovimiento(lote, TipoMovimiento.VENCIMIENTO, cantidadVencida, 
                           observaciones, usuarioId);
    }
    
    /**
     * Lista lotes próximos a vencer.
     * @param diasLimite Días desde hoy para considerar "próximo a vencer"
     */
    @Transactional(readOnly = true)
    public List<Lote> listarProximosAVencer(int diasLimite) {
        return loteRepositorio.buscarPorVencer(LocalDate.now(), LocalDate.now().plusDays(diasLimite));
    }
    
    /**
     * Lista lotes ya vencidos.
     */
    @Transactional(readOnly = true)
    public List<Lote> listarVencidos() {
        return loteRepositorio.buscarVencidos(LocalDate.now());
    }
    
    /**
     * Obtiene el historial de movimientos de un lote.
     */
    @Transactional(readOnly = true)
    public List<MovimientoStock> obtenerHistorialLote(Long loteId) {
        return loteRepositorio.findByLoteIdOrderByFechaMovimientoDesc(loteId);
    }
    
    /**
     * Registra un movimiento de stock.
     */
    private void registrarMovimiento(Lote lote, TipoMovimiento tipo, int cantidad, 
                                    String motivo, Long usuarioId) {
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepositorio.findById(usuarioId).orElse(null);
        }
        
        MovimientoStock movimiento = MovimientoStock.builder()
                .lote(lote)
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .motivo(motivo)
                .usuario(usuario)
                .build();
        
        movimientoRepositorio.save(movimiento);
    }
    
    /**
     * Obtiene un lote con su medicamento.
     */
    @Transactional(readOnly = true)
    public Lote obtenerLoteConMedicamento(Long loteId) {
        return loteRepositorio.buscarConMedicamento(loteId);
    }
}