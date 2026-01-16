package com.farmaciasalud.modulos.pacientes.dominio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_documento", unique = true, nullable = false, length = 20)
    private String numeroDocumento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    @Builder.Default
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Domicilio> domicilios = new ArrayList<>();
    
    public void agregarDomicilio(Domicilio domicilio) {
        domicilios.add(domicilio);
        domicilio.setPersona(this);
    }
    
    public void removerDomicilio(Domicilio domicilio) {
        domicilios.remove(domicilio);
        domicilio.setPersona(null);
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
