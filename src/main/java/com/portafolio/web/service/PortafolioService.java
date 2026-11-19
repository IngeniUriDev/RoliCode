package com.portafolio.web.service;

import com.portafolio.web.model.ProyectoDemo;
import com.portafolio.web.model.Servicio;
import com.portafolio.web.model.SolicitudContacto;
import com.portafolio.web.repository.SolicitudContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// @Service: Le indica a Spring Boot que esta clase es el "Chef" (Lógica de Negocio)
@Service
public class PortafolioService {

    // 1. El Chef declara que necesita al Encargado del Almacén (Repository)
    private final SolicitudContactoRepository solicitudRepository;

    // 2. Inyección de Dependencias: Spring conecta el Repository automáticamente
    public PortafolioService(SolicitudContactoRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    // Prepara el catálogo de Servicios usando tu molde Servicio.java
    public List<Servicio> obtenerCatalogoServicios() {
        return List.of(
                new Servicio(1L, "landingpage", "Landing Page Profesional",
                        "Página de muestra moderna optimizada para captar clientes"),
                new Servicio(2L, "onlineStore", "Tienda en Línea (E-Commerce)",
                        "Catálogo de productos interactivo con carrito de compras"),
                new Servicio(3L, "webApi", "Desarrollo e Integración de APIs",
                        "Extracción y conexión de información mediante APIs REST con Spring Boot"));
    }

    // Prepara la lista de Proyectos Demo usando tu molde ProyectoDemo.java
    public List<ProyectoDemo> obtenerProyectosDemo() {
        return List.of(
                new ProyectoDemo(1L, "Sistema de Inventario y Ventas (POS)",
                        "Control completo de stock, entradas de almacén, salidas y registro de ventas en SQL", "#inventario", true),
                new ProyectoDemo(2L, "Calculadora de Cotizaciones",
                        "Simulador interactivo para calcular presupuestos de desarrollo web", "#cotizador", false),
                new ProyectoDemo(3L, "Conversor de Divisas", "Calculadora de tipos de cambio en tiempo real",
                        "#divisas", false));
    }

    // Regla de Negocio + Guardar en Base de Datos SQL (POST)
    public SolicitudContacto guardarSolicitud(SolicitudContacto nuevaSolicitud) {
        if (nuevaSolicitud.getPresupuesto() != null && nuevaSolicitud.getPresupuesto() < 100) {
            throw new IllegalArgumentException("El presupuesto mínimo es de $100 USD");
        }
        return solicitudRepository.save(nuevaSolicitud);
    }

    // Método creado por Uriel: Consultar todas las solicitudes guardadas en SQL (GET)
    public List<SolicitudContacto> obtenerSolicitudes() {
        return solicitudRepository.findAll();
    }

    // Cumplimiento Legal (Derechos ARCO / GDPR): Baja de correo electrónico y borrado de datos
    @Transactional
    public long darDeBajaCorreo(String email) {
        long encontrados = solicitudRepository.countByEmailIgnoreCase(email);
        if (encontrados == 0) {
            throw new IllegalArgumentException("No se encontraron registros con el correo: " + email);
        }
        solicitudRepository.deleteByEmailIgnoreCase(email);
        return encontrados;
    }
}
