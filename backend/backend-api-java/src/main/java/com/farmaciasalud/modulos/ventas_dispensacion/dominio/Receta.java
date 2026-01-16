package com.farmaciasalud.modulos.ventas_dispensacion.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_clinica_id")
    private HistoriaClinica historiaClinica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @Column(name = "fecha_emision")
    @Builder.Default
    private LocalDateTime fechaEmision = LocalDateTime.now();
    
    @Column(name = "vigencia_dias")
    @Builder.Default
    private Integer vigenciaDias = 30;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
    
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecetaDetalle> detalles = new ArrayList<>();
    
    public void agregarDetalle(RecetaDetalle detalle) {
        detalles.add(detalle);
        detalle.setReceta(this);
    }
    
    public boolean estaVigente() {
        return fechaEmision.plusDays(vigenciaDias).isAfter(LocalDateTime.now());
    }
    
    public boolean estaCompletamenteDispensada() {
        return detalles.stream().allMatch(RecetaDetalle::estaDispensadaTotalmente);
    }
    
    public LocalDateTime getFechaVencimiento() {
        return fechaEmision.plusDays(vigenciaDias);
    }
}