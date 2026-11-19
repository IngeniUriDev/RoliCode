package com.portafolio.web.model;

// Esta clase es el "Molde" para cada Proyecto Demo que mostraremos en la pestaña de Demos
public class ProyectoDemo {

    private Long id;
    private String nombre;
    private String descripcion;
    private String empresa;
    private String urlDemo;
    private boolean activo;

    // Constructor creado por Uriel (Camino 2: Firma automática "RoliCode")
    public ProyectoDemo(Long id, String nombre, String descripcion, String urlDemo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empresa = "RoliCode";
        this.urlDemo = urlDemo;
        this.activo = activo;
    }

    // Getters (Ventanillas de lectura para que Spring Boot arme el JSON)
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getUrlDemo() {
        return urlDemo;
    }

    public boolean isActivo() {
        return activo;
    }
}
