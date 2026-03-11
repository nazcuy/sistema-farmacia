package com.farmaciasalud.modulos.pacientes.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.Sexo;
import com.farmaciasalud.modulos.pacientes.dominio.TipoDocumento;
import com.farmaciasalud.modulos.pacientes.dominio.TipoDomicilio;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaRegistroDTO {
    
    // --- Datos Personales ---
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    private String numeroDocumento;
    
    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;
    
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;
    
    private Sexo sexo;
    
    @Size(min = 8, max = 20, message = "El teléfono debe tener entre 8 y 20 caracteres")
    private String telefono;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;

    // --- Datos de Domicilio Actualizados ---
    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 255, message = "La calle no puede exceder 255 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 10, message = "El número no puede exceder 10 caracteres")
    private String numero;

    @Size(max = 10, message = "El piso no puede exceder 10 caracteres")
    private String piso;

    @Size(max = 10, message = "El departamento no puede exceder 10 caracteres")
    private String depto;

    @Size(max = 50, message = "El número de manzana no puede exceder 50 caracteres")
    private String manzana;

    @Size(max = 50, message = "El número de cuadrícula no puede exceder 50 caracteres")
    private String cuadricula;

    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    private String barrio;

    @NotBlank(message = "La localidad es obligatoria")
    @Size(max = 100, message = "La localidad no puede exceder 100 caracteres")
    private String localidad;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String provincia;

    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    private String codigoPostal;

    private TipoDomicilio tipoDomicilio;

    public String getNumeroDocumento() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}