package com.portafolio.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// @Entity: Tabla SQL para guardar el historial de cada Venta y su Ganancia
@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId;
    private String nombreProducto;
    private int cantidad;
    private Double totalVenta;     // precioVenta * cantidad
    private Double gananciaNeta;   // (precioVenta - precioCompra) * cantidad

    public Venta() {
    }

    public Venta(Long productoId, String nombreProducto, int cantidad, Double totalVenta, Double gananciaNeta) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.totalVenta = totalVenta;
        this.gananciaNeta = gananciaNeta;
    }

    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public Double getTotalVenta() { return totalVenta; }
    public Double getGananciaNeta() { return gananciaNeta; }
}
