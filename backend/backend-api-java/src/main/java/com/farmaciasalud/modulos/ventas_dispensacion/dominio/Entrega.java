package com.farmaciasalud.modulos.ventas_dispensacion.dominio;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin:
 * https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import java.math.BigDecimal;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_entrega_id", nullable = false)
    private Usuario usuarioEntrega;

    @Column(name = "fecha_entrega")
    @Builder.Default
    private LocalDateTime fechaEntrega = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "entrega", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EntregaDetalle> detalles = new ArrayList<>();

    public void agregarDetalle(EntregaDetalle detalle) {
        detalles.add(detalle);
        detalle.setEntrega(this);
    }

    public Integer getTotalItems() {
        return detalles.stream()
                .mapToInt(EntregaDetalle::getCantidadEntregada)
                .sum();
    }

    public BigDecimal getValorTotal() {
        return detalles.stream()
                .map(det -> det.getLote().getMedicamento().getPrecioVenta())
                .filter(precio -> precio != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
