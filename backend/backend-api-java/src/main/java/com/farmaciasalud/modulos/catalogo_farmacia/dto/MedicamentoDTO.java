package com.farmaciasalud.modulos.catalogo_farmacia.dto;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.FormaFarmaceutica;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.UnidadPresentacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoDTO {
    
    @Size(max = 50, message = "El código de barras no puede exceder 50 caracteres")
    private String codigoBarras;
    
    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre comercial debe tener entre 2 y 150 caracteres")
    private String nombreComercial;
    
    @Size(max = 150, message = "El nombre genérico no puede exceder 150 caracteres")
    private String nombreGenerico;
    
    @Size(max = 100, message = "El laboratorio no puede exceder 100 caracteres")
    private String laboratorio;
    
    private FormaFarmaceutica formaFarmaceutica;
    
    @Size(max = 50, message = "La concentración no puede exceder 50 caracteres")
    private String concentracion;
    
    private UnidadPresentacion unidadPresentacion;
    
    @Builder.Default
    private Boolean requiereReceta = false;
}