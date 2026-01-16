package com.farmaciasalud.modulos.pacientes.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "domicilios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Domicilio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @Column(nullable = false, length = 255)
    private String direccion;
    
    @Column(length = 100)
    private String barrio;
    
    @Column(length = 100)
    private String localidad;
    
    @Column(length = 100)
    private String provincia;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private TipoDomicilio tipo = TipoDomicilio.CASA;
}
