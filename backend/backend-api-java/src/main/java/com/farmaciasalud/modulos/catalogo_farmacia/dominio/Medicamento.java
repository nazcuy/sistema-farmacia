package com.farmaciasalud.modulos.catalogo_farmacia.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;
    
    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;
    
    @Column(name = "nombre_generico", length = 150)
    private String nombreGenerico;
    
    private String laboratorio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_farmaceutica")
    private FormaFarmaceutica formaFarmaceutica;
    
    private String concentracion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_presentacion")
    private UnidadPresentacion unidadPresentacion;
    
    @Column(name = "requiere_receta")
    @Builder.Default
    private Boolean requiereReceta = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Lote> lotes = new ArrayList<>();
    
    public Integer getStockTotal() {
        return lotes.stream()
                .mapToInt(Lote::getCantidadActual)
                .sum();
    }
    
    public boolean tieneStock() {
        return getStockTotal() > 0;
    }
    
    public boolean estaVencido() {
        return lotes.stream()
                .anyMatch(lote -> lote.getFechaVencimiento().isBefore(java.time.LocalDate.now()));
    }
}
