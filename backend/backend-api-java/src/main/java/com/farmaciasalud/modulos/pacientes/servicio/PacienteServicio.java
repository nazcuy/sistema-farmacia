package com.farmaciasalud.modulos.pacientes.servicio;
/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.dto.PersonaRegistroDTO;
import com.farmaciasalud.modulos.pacientes.dto.PersonaActualizacionDTO;
import com.farmaciasalud.modulos.pacientes.repositorio.PersonaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PacienteServicio {
    private final PersonaRepositorio personaRepositorio;
    
    /**
     * Lista todas las personas activas en el sistema.
     * Se usa para mostrar grids de búsqueda en el frontend.
     */
    @Transactional(readOnly = true)
    public List<Persona> listarTodos() {
        return personaRepositorio.findByActivoTrue();
    }
    
    /**
     * Busca una persona por su ID.
     * Retorna Optional para manejar elegantemente el caso de no encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorId(Long id) {
        return personaRepositorio.findById(id);
    }
    
    /**
     * Busca una persona por su número de documento (DNI, etc.).
     * Este es el método más usado para identificar pacientes en mostrador.
     */
    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorDocumento(String numeroDocumento) {
        return personaRepositorio.findByNumeroDocumento(numeroDocumento);
    }
    
    /**
     * Busca personas por apellido (búsqueda parcial).
     * Útil para cuando el usuario no recuerda el documento exacto.
     */
    @Transactional(readOnly = true)
    public List<Persona> buscarPorApellido(String apellido) {
        return personaRepositorio.findByApellidoContainingIgnoreCase(apellido);
    }
    
    /**
     * Busca personas por nombre o apellido.
     * Búsqueda general para el campo de búsqueda rápida.
     */
    @Transactional(readOnly = true)
    public List<Persona> buscarPorNombreOApellido(String termino) {
        return personaRepositorio.buscarPorNombreOApellido(termino, termino);
    }
    
    /**
     * Registra una nueva persona en el sistema.
     * Valida que no exista otra persona con el mismo documento.
     */
    public Persona registrar(PersonaRegistroDTO dto) {
        // Validación: verificar que no exista duplicado por documento
        if (personaRepositorio.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new IllegalArgumentException("Ya existe una persona registrada con el documento: " + dto.getNumeroDocumento());
        }
        
        // Conversión de DTO a entidad
        Persona persona = Persona.builder()
                .numeroDocumento(dto.getNumeroDocumento())
                .tipoDocumento(dto.getTipoDocumento())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .fechaNacimiento(dto.getFechaNacimiento())
                .sexo(dto.getSexo())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .build();
        
        return personaRepositorio.save(persona);
    }
    
    /**
     * Actualiza los datos de una persona existente.
     * Solo actualiza campos permitidos (no el documento que es identificador).
     */
    public Persona actualizar(Long id, PersonaActualizacionDTO dto) {
        Persona persona = personaRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + id));
        
        // Actualizar solo los campos que vienen en el DTO
        if (dto.getNombre() != null) {
            persona.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            persona.setApellido(dto.getApellido());
        }
        if (dto.getFechaNacimiento() != null) {
            persona.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getSexo() != null) {
            persona.setSexo(dto.getSexo());
        }
        if (dto.getTelefono() != null) {
            persona.setTelefono(dto.getTelefono());
        }
        if (dto.getEmail() != null) {
            persona.setEmail(dto.getEmail());
        }
        
        return personaRepositorio.save(persona);
    }
    
    /**
     * Desactiva una persona (soft delete).
     * No la eliminamos físicamente para mantener historial y trazabilidad.
     */
    public void eliminar(Long id) {
        Persona persona = personaRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + id));
        
        persona.setActivo(false);
        personaRepositorio.save(persona);
    }
    
    /**
     * Obtiene una persona con sus domicilios cargados.
     * Útil para pantallas de edición o visualización completa.
     */
    @Transactional(readOnly = true)
    public Optional<Persona> buscarConDomicilios(Long id) {
        return personaRepositorio.buscarConDomicilios(id);
    }
    
    /**
     * Obtiene la cantidad total de personas activas.
     * Útil para reportes y estadísticas.
     */
    @Transactional(readOnly = true)
    public long contarPersonas() {
        return personaRepositorio.contarPersonasActivas();
    }
}
