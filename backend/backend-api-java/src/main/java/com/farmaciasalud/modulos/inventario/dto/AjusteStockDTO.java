package com.farmaciasalud.modulos.inventario.dto;

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
public class AjusteStockDTO {
    
    @NotNull(message = "El ID del lote es obligatorio")
    private Long loteId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 5, max = 255, message = "El motivo debe tener entre 5 y 255 caracteres")
    private String motivo;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
}