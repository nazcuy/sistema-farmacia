package com.farmaciasalud.modulos.pacientes.dominio;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin:
 * https://www.linkedin.com/in/nicolas-azcuy-prog/
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

    public static Object builder() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(nullable = false, length = 255)
    private String calle;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 10)
    private String piso;

    @Column(length = 10)
    private String depto;

    @Column(length = 50)
    private String manzana;

    @Column(length = 50)
    private String cuadricula;

    @Column(length = 100)
    private String barrio;

    @Column(nullable = false, length = 100)
    private String localidad;

    @Column(nullable = false, length = 100)
    private String provincia;

    @Column(name = "codigo_postal", nullable = false, length = 10)
    private String codigoPostal;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private TipoDomicilio tipo = TipoDomicilio.CASA;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}