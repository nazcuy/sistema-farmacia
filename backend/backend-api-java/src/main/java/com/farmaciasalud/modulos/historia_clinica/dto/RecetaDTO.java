package com.farmaciasalud.modulos.historia_clinica.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.validation.Valid;
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
public class RecetaDTO {
    
    @Min(value = 1, message = "La vigencia mínima es 1 día")
    @Max(value = 365, message = "La vigencia máxima es 365 días")
    private Integer vigenciaDias;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
    
    @Valid
    private List<RecetaDetalleDTO> detalles;
}