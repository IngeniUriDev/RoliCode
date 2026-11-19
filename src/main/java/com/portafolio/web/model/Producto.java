package com.portafolio.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// @Entity: Convierte esta clase en la tabla SQL "PRODUCTO" para nuestro Sistema de Inventario
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos diseñados por Uriel:
    private String codigo;
    private String nombre;
    private Double precioCompra;
    private Double precioVenta;
    private int stockActual;

    // Constructor vacío obligatorio para JPA (Base de Datos)
    public Producto() {
    }

    // Constructor completo para crear productos fácilmente
    public Producto(String codigo, String nombre, Double precioCompra, Double precioVenta, int stockActual) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
    }

    // Getters y Setters (Ventanillas de lectura y modificación)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }

    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
}
