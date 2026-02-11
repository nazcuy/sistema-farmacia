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
public class HistoriaClinicaDTO {
    
    @NotNull(message = "El ID de la persona es obligatorio")
    private Long personaId;
    
    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 1000, message = "El motivo no puede exceder 1000 caracteres")
    private String motivoConsulta;
    
    @Size(max = 1000, message = "El diagnóstico no puede exceder 1000 caracteres")
    private String diagnostico;
    
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;
    
    @Valid
    private List<RecetaDTO> recetas;
}