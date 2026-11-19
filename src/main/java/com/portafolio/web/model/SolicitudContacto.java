package com.portafolio.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 1. @Entity: Le indica a Spring Boot / JPA que cree una tabla SQL para esta clase
@Entity
public class SolicitudContacto {

    // 2. @Id + @GeneratedValue: Llave primaria autogenerada (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCliente;
    private String email;
    private String servicioInteres;
    private Double presupuesto;
    private String mensaje;

    // Constructor vacío requerido por JPA (Base de Datos)
    public SolicitudContacto() {
    }

    // Constructor con parámetros para crear solicitudes fácilmente
    public SolicitudContacto(String nombreCliente, String email, String servicioInteres, Double presupuesto, String mensaje) {
        this.nombreCliente = nombreCliente;
        this.email = email;
        this.servicioInteres = servicioInteres;
        this.presupuesto = presupuesto;
        this.mensaje = mensaje;
    }

    // Getters y Setters (Ventanillas de lectura y escritura)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getServicioInteres() { return servicioInteres; }
    public void setServicioInteres(String servicioInteres) { this.servicioInteres = servicioInteres; }

    public Double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(Double presupuesto) { this.presupuesto = presupuesto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
