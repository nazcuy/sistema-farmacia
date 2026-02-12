package com.farmaciasalud.modulos.inventario.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.farmaciasalud.modulos.pacientes.dominio.Usuario;

@Entity
@Table(name = "movimientos_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private TipoMovimiento tipoMovimiento;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(length = 255)
    private String motivo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Column(name = "fecha_movimiento")
    @Builder.Default
    private LocalDateTime fechaMovimiento = LocalDateTime.now();
    
    @Column(name = "referencia_tabla")
    private String referenciaTabla;
    
    @Column(name = "referencia_id")
    private Long referenciaId;
}