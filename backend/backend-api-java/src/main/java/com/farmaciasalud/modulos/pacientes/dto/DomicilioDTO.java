package com.farmaciasalud.modulos.pacientes.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin:
 * https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.TipoDomicilio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomicilioDTO {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 255, message = "La calle no puede exceder 255 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 10, message = "El número no puede exceder 10 caracteres")
    private String numero; // String para soportar casos como "123A" o "S/N"

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

    private TipoDomicilio tipo;
}
