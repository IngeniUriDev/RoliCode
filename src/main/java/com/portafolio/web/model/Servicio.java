package com.portafolio.web.model;

// Esta clase es el "Molde" de cada servicio que ofrecerás en tu Landing Page
public class Servicio {

    private Long id;
    private String codigo;
    private String titulo;
    private String descripcion;

    public Servicio(Long id, String codigo, String titulo, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
}
