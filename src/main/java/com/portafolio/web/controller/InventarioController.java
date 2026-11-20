package com.portafolio.web.controller;

import com.portafolio.web.model.Producto;
import com.portafolio.web.model.Venta;
import com.portafolio.web.service.InventarioService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

// @RestController: El "Mesero" encargado de atender el Demo de Inventario y Ventas
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // Listar todo el stock actual -> GET /api/inventario/productos
    @GetMapping("/productos")
    public List<Producto> obtenerProductos() {
        return inventarioService.listarProductos();
    }

    // Crear un nuevo producto -> POST /api/inventario/productos
    @PostMapping("/productos")
    public Producto agregarProducto(@RequestBody Producto producto) {
        return inventarioService.crearProducto(producto);
    }

    // Registrar una ENTRADA de almacén -> POST
    // /api/inventario/entrada?idProducto=1&cantidad=5
    @PostMapping("/entrada")
    public Producto entradaAlmacen(@RequestParam Long idProducto, @RequestParam int cantidad) {
        return inventarioService.registrarEntrada(idProducto, cantidad);
    }

    // Registrar una SALIDA de almacén -> POST
    // /api/inventario/salida?idProducto=1&cantidad=2
    @PostMapping("/salida")
    public Producto salidaAlmacen(@RequestParam Long idProducto, @RequestParam int cantidad) {
        return inventarioService.registrarSalida(idProducto, cantidad);
    }

    // Registrar una VENTA (descuenta stock y calcula ganancia) -> POST
    // /api/inventario/venta?idProducto=1&cantidad=1
    @PostMapping("/venta")
    public Venta realizarVenta(@RequestParam Long idProducto, @RequestParam int cantidad) {
        return inventarioService.registrarVenta(idProducto, cantidad);
    }

    // Consultar el historial de ventas y ganancias -> GET /api/inventario/ventas
    @GetMapping("/ventas")
    public List<Venta> obtenerHistorialVentas() {
        return inventarioService.listarVentas();
    }

    // Eliminar un producto (Programado por Uriel) -> DELETE
    // /api/inventario/productos?idProducto=1
    @DeleteMapping("/productos")
    public void borrarProducto(@RequestParam Long idProducto) {
        inventarioService.eliminarProducto(idProducto);
    }

    // Buscar productos por nombre -> GET /api/inventario/buscar?texto=teclado
    @GetMapping("/buscar")
    public List<Producto> buscarProductos(@RequestParam String texto) {
        return inventarioService.buscarPorNombre(texto);
    }

    // Filtrar productos con stock bajo -> GET /api/inventario/stock-bajo?limite=5
    @GetMapping("/stock-bajo")
    public List<Producto> obtenerStockBajo(@RequestParam(defaultValue = "5") int limite) {
        return inventarioService.filtrarStockBajo(limite);
    }
}
