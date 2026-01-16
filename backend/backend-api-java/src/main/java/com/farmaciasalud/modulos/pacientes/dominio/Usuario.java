package com.farmaciasalud.modulos.pacientes.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public boolean esAdmin() {
        return rol == Rol.ADMINISTRADOR;
    }
    
    public boolean esMedico() {
        return rol == Rol.MEDICO;
    }
    
    public boolean esFarmaceutico() {
        return rol == Rol.FARMACEUTICO;
    }
    
    public boolean esAgente() {
        return rol == Rol.AGENTE;
    }
}