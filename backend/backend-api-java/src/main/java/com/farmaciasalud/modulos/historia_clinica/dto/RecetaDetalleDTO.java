package com.farmaciasalud.modulos.historia_clinica.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaDetalleDTO {
    
    @NotNull(message = "El ID del medicamento es obligatorio")
    private Long medicamentoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
    
    @Size(max = 255, message = "La dosificación no puede exceder 255 caracteres")
    private String dosificacion;
}