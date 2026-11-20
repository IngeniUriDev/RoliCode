package com.portafolio.web.repository;

import com.portafolio.web.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// El "Encargado de la Bodega de Productos" en la Base de Datos SQL.
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscador por nombre (ignorando mayúsculas y minúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String texto);

    // Filtro de Alerta de Stock Bajo (Programado por Uriel): WHERE stock_actual <=
    // limite
    List<Producto> findByStockActualLessThanEqual(int limite);
}
