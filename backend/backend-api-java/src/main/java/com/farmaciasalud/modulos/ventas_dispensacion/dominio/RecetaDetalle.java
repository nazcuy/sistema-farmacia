package com.farmaciasalud.modulos.ventas_dispensacion.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "receta_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(length = 255)
    private String dosificacion;
    
    @Column(name = "cantidad_dispensada")
    @Builder.Default
    private Integer cantidadDispensada = 0;
    
    public boolean estaDispensadaTotalmente() {
        return cantidadDispensada >= cantidad;
    }
    
    public Integer getCantidadPendiente() {
        return Math.max(0, cantidad - cantidadDispensada);
    }
    
    public void registrarDispensacion(int cantidad) {
        if (cantidad > getCantidadPendiente()) {
            throw new RuntimeException("No se puede dispensar más de lo prescrito");
        }
        this.cantidadDispensada += cantidad;
    }
}