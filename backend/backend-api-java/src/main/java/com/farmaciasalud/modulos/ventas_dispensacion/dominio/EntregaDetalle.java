package com.farmaciasalud.modulos.ventas_dispensacion.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entrega_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrega_id", nullable = false)
    private Entrega entrega;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;
    
    @Column(name = "cantidad_entregada", nullable = false)
    private Integer cantidadEntregada;
    
    public Integer getSubtotal() {
        return cantidadEntregada;
    }
}