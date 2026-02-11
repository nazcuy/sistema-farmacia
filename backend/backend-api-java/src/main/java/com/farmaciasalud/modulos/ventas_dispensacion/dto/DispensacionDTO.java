package com.farmaciasalud.modulos.ventas_dispensacion.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensacionDTO {
    
    @NotNull(message = "El ID de la persona es obligatorio")
    private Long personaId;
    
    private Long recetaId;
    
    @NotNull(message = "El ID del usuario que entrega es obligatorio")
    private Long usuarioEntregaId;
    
    @NotEmpty(message = "Debe incluir al menos un item")
    @Valid
    private List<ItemDispensacionDTO> items;
    
    private String observaciones;
}