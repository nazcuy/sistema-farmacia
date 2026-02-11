package com.farmaciasalud.modulos.historia_clinica.servicio;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin:
 * https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.modulos.historia_clinica.dominio.HistoriaClinica;
import com.farmaciasalud.modulos.historia_clinica.dto.HistoriaClinicaDTO;
import com.farmaciasalud.modulos.historia_clinica.repositorio.HistoriaClinicaRepositorio;
import com.farmaciasalud.modulos.historia_clinica.dto.RecetaDTO;
import com.farmaciasalud.modulos.pacientes.dominio.Persona;
import com.farmaciasalud.modulos.pacientes.dominio.Usuario;
import com.farmaciasalud.modulos.pacientes.repositorio.PersonaRepositorio;
import com.farmaciasalud.modulos.pacientes.repositorio.UsuarioRepositorio;
import com.farmaciasalud.modulos.catalogo_farmacia.dominio.Medicamento;
import com.farmaciasalud.modulos.catalogo_farmacia.repositorio.MedicamentoRepositorio;
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.Receta;
import com.farmaciasalud.modulos.ventas_dispensacion.dominio.RecetaDetalle;
import com.farmaciasalud.modulos.ventas_dispensacion.repositorio.RecetaRepositorio;
import com.farmaciasalud.nucleo.seguridad.Rol;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoriaClinicaServicio {

    private final HistoriaClinicaRepositorio historiaRepositorio;
    private final RecetaRepositorio recetaRepositorio;
    private final PersonaRepositorio personaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;

    @Transactional(readOnly = true)
    public List<HistoriaClinica> listarPorPaciente(Long personaId) {
        return historiaRepositorio.findByPersonaIdOrderByFechaConsultaDesc(personaId);
    }

    @Transactional(readOnly = true)
    public Optional<HistoriaClinica> obtenerUltima(Long personaId) {
        return Optional.ofNullable(historiaRepositorio.buscarUltimaHistoria(personaId));
    }

    @Transactional(readOnly = true)
    public Optional<HistoriaClinica> obtenerConRecetas(Long historiaId) {
        return Optional.ofNullable(historiaRepositorio.buscarConRecetas(historiaId));
    }

    public HistoriaClinica registrarConsulta(HistoriaClinicaDTO dto, Long medicoId) {
        Persona paciente = personaRepositorio.findById(dto.getPersonaId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        Usuario medico = usuarioRepositorio.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        if (medico.getRol() != Rol.MEDICO) {
            throw new IllegalStateException("Solo un médico puede crear historias clínicas");
        }

        HistoriaClinica historia = HistoriaClinica.builder()
                .persona(paciente)
                .medico(medico)
                .motivoConsulta(dto.getMotivoConsulta())
                .diagnostico(dto.getDiagnostico())
                .observaciones(dto.getObservaciones())
                .build();

        historia = historiaRepositorio.save(historia);

        if (dto.getRecetas() != null && !dto.getRecetas().isEmpty()) {
            for (RecetaDTO recetaDTO : dto.getRecetas()) {
                Receta receta = crearReceta(recetaDTO, historia, medico, paciente);
                historia.agregarReceta(receta);
            }
        }

        return historiaRepositorio.save(historia);
    }

    private Receta crearReceta(RecetaDTO dto, HistoriaClinica historia, Usuario medico, Persona paciente) {
        Receta receta = Receta.builder()
                .historiaClinica(historia)
                .medico(medico)
                .persona(paciente)
                .observaciones(dto.getObservaciones())
                .vigenciaDias(dto.getVigenciaDias() != null ? dto.getVigenciaDias() : 30)
                .build();

        if (dto.getDetalles() != null) {
            for (com.farmaciasalud.modulos.historia_clinica.dto.RecetaDetalleDTO detalleDTO : dto.getDetalles()) {
                Medicamento medicamento = medicamentoRepositorio.findById(detalleDTO.getMedicamentoId())
                        .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));

                RecetaDetalle detalle = RecetaDetalle.builder()
                        .medicamento(medicamento)
                        .cantidad(detalleDTO.getCantidad())
                        .dosificacion(detalleDTO.getDosificacion())
                        .build();

                receta.agregarDetalle(detalle);
            }
        }

        return recetaRepositorio.save(receta);
    }

    @Transactional(readOnly = true)
    public List<HistoriaClinica> buscarPorFechas(Long personaId, LocalDateTime inicio, LocalDateTime fin) {
        return historiaRepositorio.buscarPorPersonaYFechas(personaId, inicio, fin);
    }
}
