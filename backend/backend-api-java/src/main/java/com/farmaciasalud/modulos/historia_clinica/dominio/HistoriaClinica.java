package com.farmaciasalud.modulos.historia_clinica.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historias_clinicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaClinica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;
    
    @Column(name = "fecha_consulta")
    @Builder.Default
    private LocalDateTime fechaConsulta = LocalDateTime.now();
    
    @Column(name = "motivo_consulta", columnDefinition = "TEXT")
    private String motivoConsulta;
    
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Receta> recetas = new ArrayList<>();
    
    public void agregarReceta(Receta receta) {
        recetas.add(receta);
        receta.setHistoriaClinica(this);
    }
    
    public void removerReceta(Receta receta) {
        recetas.remove(receta);
        receta.setHistoriaClinica(null);
    }
    
    public int getTotalRecetas() {
        return recetas.size();
    }
}