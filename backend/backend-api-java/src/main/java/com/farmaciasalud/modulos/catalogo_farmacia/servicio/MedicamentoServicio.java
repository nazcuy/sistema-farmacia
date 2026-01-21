package com.farmaciasalud.modulos.catalogo_farmacia.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.FormaFarmaceutica;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.UnidadPresentacion;
import com.farmaciasalud.modulos.catalogo_farmacia.dto.MedicamentoDTO;
import com.farmaciasalud.modulos.catalogo_farmacia.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicamentoServicio {
    private final MedicamentoRepositorio medicamentoRepositorio;
    
    /**
     * Lista todos los medicamentos activos ordenados por nombre comercial.
     */
    @Transactional(readOnly = true)
    public List<Medicamento> listarTodos() {
        return medicamentoRepositorio.findByActivoTrueOrderByNombreComercialAsc();
    }
    
    /**
     * Busca un medicamento por su código de barras.
     * Este es el método principal para buscar en el punto de venta.
     */
    @Transactional(readOnly = true)
    public Optional<Medicamento> buscarPorCodigoBarras(String codigoBarras) {
        return medicamentoRepositorio.findByCodigoBarrasAndActivoTrue(codigoBarras);
    }
    
    /**
     * Búsqueda general por texto.
     * Busca en nombre comercial, nombre genérico y código de barras.
     */
    @Transactional(readOnly = true)
    public List<Medicamento> buscar(String termino) {
        return medicamentoRepositorio.buscarPorTexto(termino);
    }
    
    /**
     * Busca por nombre comercial (búsqueda parcial).
     */
    @Transactional(readOnly = true)
    public List<Medicamento> buscarPorNombre(String nombre) {
        return medicamentoRepositorio.findByNombreComercialContainingIgnoreCase(nombre);
    }
    
    /**
     * Filtra medicamentos por forma farmacéutica.
     */
    @Transactional(readOnly = true)
    public List<Medicamento> buscarPorForma(FormaFarmaceutica forma) {
        return medicamentoRepositorio.findByFormaFarmaceutica(forma);
    }
    
    /**
     * Lista medicamentos que no requieren receta.
     * Útil para mostrar en categoría de venta libre.
     */
    @Transactional(readOnly = true)
    public List<Medicamento> listarVentaLibre() {
        return medicamentoRepositorio.findByRequiereRecetaFalse();
    }
    
    /**
     * Registra un nuevo medicamento en el catálogo.
     */
    public Medicamento registrar(MedicamentoDTO dto) {
        // Validar código de barras único
        if (dto.getCodigoBarras() != null && 
            medicamentoRepositorio.existsByCodigoBarras(dto.getCodigoBarras())) {
            throw new IllegalArgumentException("Ya existe un medicamento con código de barras: " + dto.getCodigoBarras());
        }
        
        Medicamento medicamento = Medicamento.builder()
                .codigoBarras(dto.getCodigoBarras())
                .nombreComercial(dto.getNombreComercial())
                .nombreGenerico(dto.getNombreGenerico())
                .laboratorio(dto.getLaboratorio())
                .formaFarmaceutica(dto.getFormaFarmaceutica())
                .concentracion(dto.getConcentracion())
                .unidadPresentacion(dto.getUnidadPresentacion())
                .requiereReceta(dto.getRequiereReceta() != null ? dto.getRequiereReceta() : false)
                .build();
        
        return medicamentoRepositorio.save(medicamento);
    }
    
    /**
     * Actualiza un medicamento existente.
     * Solo campos no nulos se actualizan.
     */
    public Medicamento actualizar(Long id, MedicamentoDTO dto) {
        Medicamento medicamento = medicamentoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado con ID: " + id));
        
        // Actualizar campos si vienen en el DTO
        if (dto.getNombreComercial() != null) {
            medicamento.setNombreComercial(dto.getNombreComercial());
        }
        if (dto.getNombreGenerico() != null) {
            medicamento.setNombreGenerico(dto.getNombreGenerico());
        }
        if (dto.getLaboratorio() != null) {
            medicamento.setLaboratorio(dto.getLaboratorio());
        }
        if (dto.getFormaFarmaceutica() != null) {
            medicamento.setFormaFarmaceutica(dto.getFormaFarmaceutica());
        }
        if (dto.getConcentracion() != null) {
            medicamento.setConcentracion(dto.getConcentracion());
        }
        if (dto.getUnidadPresentacion() != null) {
            medicamento.setUnidadPresentacion(dto.getUnidadPresentacion());
        }
        if (dto.getRequiereReceta() != null) {
            medicamento.setRequiereReceta(dto.getRequiereReceta());
        }
        
        return medicamentoRepositorio.save(medicamento);
    }
    
    /**
     * Desactiva un medicamento (soft delete).
     * No se puede eliminar un medicamento que tiene stock.
     */
    public void eliminar(Long id) {
        Medicamento medicamento = medicamentoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado con ID: " + id));
        
        // Verificar que no tenga stock antes de desactivar
        if (medicamento.getStockTotal() > 0) {
            throw new IllegalStateException("No se puede desactivar un medicamento con stock disponible");
        }
        
        medicamento.setActivo(false);
        medicamentoRepositorio.save(medicamento);
    }
    
    /**
     * Obtiene un medicamento con sus lotes.
     * Útil para pantalla de inventario.
     */
    @Transactional(readOnly = true)
    public Optional<Medicamento> buscarConStock(Long id) {
        return medicamentoRepositorio.buscarConLotes(id);
    }
    
    /**
     * Lista todos los laboratorios únicos.
     */
    @Transactional(readOnly = true)
    public List<String> listarLaboratorios() {
        return medicamentoRepositorio.listarLaboratorios();
    }
    
    /**
     * Obtiene la cantidad de medicamentos activos.
     */
    @Transactional(readOnly = true)
    public long contar() {
        return medicamentoRepositorio.contarMedicamentosActivos();
    }
}
