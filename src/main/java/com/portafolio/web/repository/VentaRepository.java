package com.portafolio.web.repository;

import com.portafolio.web.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

// El "Encargado de los Tickets de Venta" en la Base de Datos SQL
public interface VentaRepository extends JpaRepository<Venta, Long> {
}
