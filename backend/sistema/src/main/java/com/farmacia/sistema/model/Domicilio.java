package com.farmacia.sistema.model;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
        
public class Domicilio {
    private int id;
    private int personaId;
    private String provincia;
    private String municipio;
    private String barrio;
    private String direccion;
    
    private int manzana;
    private int lote;
    private String frenteManzana;
    private String entreCalle1;
    private String entreCalle2;
    private String observaciones_ubicacion;
    
    private String materialConstruccion;
    private String accesoAgua;
    private String tipoCocina;
    private String calefaccion;
    private String otraCalefaccion;
    private String conexionElectricidad;
    private String accesoInternet;
    private String ambientesDormir;
    private String situacionIngresos;
    private String asistenciaAlimentaria;
    private String tipoAsistenciaAlimentaria;
    
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private boolean actual;
    
    public Domicilio() {
    }

    public Domicilio(int id, int personaId, String provincia, String municipio, String barrio, String direccion, int manzana, int lote, String frenteManzana, String entreCalle1, String entreCalle2, String observaciones_ubicacion, String materialConstruccion, String accesoAgua, String tipoCocina, String calefaccion, String otraCalefaccion, String conexionElectricidad, String accesoInternet, String ambientesDormir, String situacionIngresos, String asistenciaAlimentaria, String tipoAsistenciaAlimentaria, LocalDate fechaDesde, LocalDate fechaHasta, boolean actual) {
        this.id = id;
        this.personaId = personaId;
        this.provincia = provincia;
        this.municipio = municipio;
        this.barrio = barrio;
        this.direccion = direccion;
        this.manzana = manzana;
        this.lote = lote;
        this.frenteManzana = frenteManzana;
        this.entreCalle1 = entreCalle1;
        this.entreCalle2 = entreCalle2;
        this.observaciones_ubicacion = observaciones_ubicacion;
        this.materialConstruccion = materialConstruccion;
        this.accesoAgua = accesoAgua;
        this.tipoCocina = tipoCocina;
        this.calefaccion = calefaccion;
        this.otraCalefaccion = otraCalefaccion;
        this.conexionElectricidad = conexionElectricidad;
        this.accesoInternet = accesoInternet;
        this.ambientesDormir = ambientesDormir;
        this.situacionIngresos = situacionIngresos;
        this.asistenciaAlimentaria = asistenciaAlimentaria;
        this.tipoAsistenciaAlimentaria = tipoAsistenciaAlimentaria;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.actual = actual;
    }
}
