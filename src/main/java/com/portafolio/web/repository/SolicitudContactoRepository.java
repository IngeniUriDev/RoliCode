package com.portafolio.web.repository;

import com.portafolio.web.model.SolicitudContacto;
import org.springframework.data.jpa.repository.JpaRepository;

// El "Encargado del Almacén" de Solicitudes de Clientes en SQL
public interface SolicitudContactoRepository extends JpaRepository<SolicitudContacto, Long> {

    // Query Method para Cumplimiento Legal (Derecho al Olvido / Baja de Correos):
    // Elimina todas las solicitudes asociadas a un correo electrónico (sin importar mayúsculas)
    void deleteByEmailIgnoreCase(String email);

    // Cuenta cuántas solicitudes existen con ese correo antes de borrarlas
    long countByEmailIgnoreCase(String email);
}
