package com.farmacia.sistema.model;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class Persona {

    private int id;
    private String documentoIdentidad;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String paisNacimiento;
    private String telefono;
    private String email;
    private LocalDateTime fechaRegistro;
    private boolean activo;

    public Persona() {
    }

    public Persona(int id, String documentoIdentidad, String nombre, String apellido, 
                   LocalDate fechaNacimiento, String sexo, String paisNacimiento, 
                   String telefono, String email, LocalDateTime fechaRegistro, boolean activo) {
        this.id = id;
        this.documentoIdentidad = documentoIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.paisNacimiento = paisNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }
}
