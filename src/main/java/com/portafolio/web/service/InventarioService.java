package com.portafolio.web.service;

import com.portafolio.web.model.Producto;
import com.portafolio.web.model.Venta;
import com.portafolio.web.repository.ProductoRepository;
import com.portafolio.web.repository.VentaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.List;

// @Service: El "Chef / Jefe de Bodega" que controla todas las reglas del Inventario y Ventas
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public InventarioService(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    // Al arrancar el servidor, cargamos 3 productos iniciales de muestra si la
    // bodega está vacía
    @PostConstruct
    public void cargarProductosIniciales() {
        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto("LAP-01", "Laptop Gamer 16GB", 650.0, 900.0, 8));
            productoRepository.save(new Producto("MON-02", "Monitor Curvo 27\"", 180.0, 260.0, 12));
            productoRepository.save(new Producto("TEC-03", "Teclado Mecánico RGB", 35.0, 65.0, 4));
        }
    }

    // Consultar todos los productos en el inventario
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Registrar un producto nuevo (Reglas de negocio: Cuota máx. 10 productos y
    // precioVenta >= precioCompra)
    public Producto crearProducto(Producto nuevo) {
        if (productoRepository.count() >= 10) {
            throw new IllegalArgumentException(
                    "Límite del Demo alcanzado: Máximo 10 productos para proteger la memoria.");
        }
        if (nuevo.getPrecioVenta() < nuevo.getPrecioCompra()) {
            throw new IllegalArgumentException(
                    "Alerta financiera: El precio de venta no puede ser menor al costo de compra.");
        }
        return productoRepository.save(nuevo);
    }

    // ENTRADA de Almacén (Abastecimiento: suma unidades al stock actual)
    public Producto registrarEntrada(Long idProducto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entrada debe ser mayor a 0");
        }
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        int nuevoStock = producto.getStockActual() + cantidad;
        producto.setStockActual(nuevoStock);
        return productoRepository.save(producto);
    }

    // SALIDA de Almacén (Programado por Uriel: valida stock y resta unidades)
    public Producto registrarSalida(Long idProducto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser mayor a 0");
        }
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Regla de negocio: No permitir stock negativo
        if (producto.getStockActual() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en bodega");
        }

        int nuevoStock = producto.getStockActual() - cantidad;
        producto.setStockActual(nuevoStock);
        return productoRepository.save(producto);
    }

    // CONTROL DE VENTAS: Descuenta del stock (usando tu método registrarSalida) y
    // guarda el Ticket de Venta
    public Venta registrarVenta(Long idProducto, int cantidad) {
        // Regla de protección de memoria programada por Uriel:
        if (ventaRepository.count() >= 50) {
            throw new IllegalArgumentException("Excediste el limite de 50 ventas del Demo");
        }
        // Primero aplicamos tu lógica de salida para validar y descontar el stock:
        Producto producto = registrarSalida(idProducto, cantidad);

        // Calculamos el dinero cobrado y aplicamos tu regla de 10% de descuento por
        // mayoreo (5 o más uds):
        Double totalCobrado = producto.getPrecioVenta() * cantidad;
        if (cantidad >= 5) {
            totalCobrado = totalCobrado * 0.90;
        }
        Double costoTotal = producto.getPrecioCompra() * cantidad;
        Double ganancia = totalCobrado - costoTotal;

        // Creamos el ticket de venta y lo guardamos en SQL:
        Venta nuevaVenta = new Venta(producto.getId(), producto.getNombre(), cantidad, totalCobrado, ganancia);
        return ventaRepository.save(nuevaVenta);
    }

    // Consultar el historial de todas las ventas realizadas
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // Eliminar un producto del inventario por su ID (Con protección mínima de 3
    // productos en el Demo)
    public void eliminarProducto(Long idProducto) {
        if (productoRepository.count() <= 3) {
            throw new IllegalArgumentException("Protección del Demo: Deben quedar al menos 3 productos de muestra.");
        }
        productoRepository.deleteById(idProducto);
    }

    // Buscar productos por nombre (usando Query Method)
    public List<Producto> buscarPorNombre(String texto) {
        return productoRepository.findByNombreContainingIgnoreCase(texto);
    }

    // Filtrar productos con stock bajo (usando el Query Method de Uriel)
    public List<Producto> filtrarStockBajo(int limite) {
        return productoRepository.findByStockActualLessThanEqual(limite);
    }
}
