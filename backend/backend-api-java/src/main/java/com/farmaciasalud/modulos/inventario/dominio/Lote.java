package com.farmaciasalud.modulos.inventario.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;
    
    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;
    
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    
    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;
    
    @Column(name = "cantidad_actual", nullable = false)
    @Builder.Default
    private Integer cantidadActual = 0;
    
    @Column(name = "fecha_ingreso")
    @Builder.Default
    private java.time.LocalDateTime fechaIngreso = java.time.LocalDateTime.now();
    
    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;
    
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MovimientoStock> movimientos = new ArrayList<>();
    
    public boolean estaVencido() {
        return fechaVencimiento.isBefore(LocalDate.now());
    }
    
    public boolean estaPorVencer(int diasLimite) {
        return fechaVencimiento.isBefore(LocalDate.now().plusDays(diasLimite));
    }
    
    public boolean tieneStock() {
        return cantidadActual > 0;
    }
    
    public void reducirStock(int cantidad) {
        if (cantidad > this.cantidadActual) {
            throw new RuntimeException("No hay suficiente stock en el lote");
        }
        this.cantidadActual -= cantidad;
    }
    
    public void aumentarStock(int cantidad) {
        this.cantidadActual += cantidad;
    }
    
    public Integer getDiasParaVencimiento() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento).intValue();
    }
}
