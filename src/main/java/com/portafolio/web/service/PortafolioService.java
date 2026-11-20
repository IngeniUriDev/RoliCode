package com.portafolio.web.service;

import com.portafolio.web.model.ProyectoDemo;
import com.portafolio.web.model.Servicio;
import com.portafolio.web.model.SolicitudContacto;
import com.portafolio.web.repository.SolicitudContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

// @Service: Le indica a Spring Boot que esta clase es el "Chef" (Lógica de Negocio)
@Service
public class PortafolioService {

    // El Chef declara que necesita al Encargado del Almacén (Repository)
    private final SolicitudContactoRepository solicitudRepository;

    // Hash SHA-256 de la clave de administrador ("rolicode2026") - Nunca se guarda
    // en texto plano
    private static final String HASH_ADMIN_SECRETO = "9b9510b1f8fcd49424a35e6848913cc6c3ad9345d43b7be1e9c43cc11a243826";

    // Contador de intentos fallidos y reloj de bloqueo anti-fuerza bruta (15
    // minutos tras 3 fallos)
    private int intentosFallidos = 0;
    private LocalDateTime bloqueadoHasta = null;

    // Inyección de Dependencias: Spring conecta el Repository automáticamente
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

    // Prepara la lista de Proyectos Demo usando tu molde ProyectoDemo.java (Los 3
    // Demos activos)
    public List<ProyectoDemo> obtenerProyectosDemo() {
        return List.of(
                new ProyectoDemo(1L, "Sistema de Inventario y Ventas (POS)",
                        "Control completo de stock, entradas de almacén, salidas y registro de ventas en SQL",
                        "#inventario", true),
                new ProyectoDemo(2L, "Calculadora de Cotizaciones (MXN)",
                        "Simulador interactivo para calcular presupuestos de desarrollo web en Pesos Mexicanos",
                        "#cotizador", true),
                new ProyectoDemo(3L, "Conversor de Divisas (MXN / Global)",
                        "Calculadora empresarial de tipos de cambio con switch moderno de Java 21", "#divisas", true));
    }

    // Motor del Demo #3: Conversor de Divisas (Programado por Uriel con switch de
    // Java 21)
    public Map<String, Object> convertirDivisaDemo(double montoMxn, String monedaDestino) {
        if (montoMxn < 0) {
            throw new IllegalArgumentException("El monto en MXN no puede ser negativo");
        }

        double tasaCambio = switch (monedaDestino) {
            case "USD" -> 20.15; // Dólar (EE. UU.)
            case "EUR" -> 21.60; // Euro (Unión Europea)
            case "CAD" -> 14.50; // Dólar Canadiense
            case "GBP" -> 25.40; // Libra Esterlina
            default -> 1.0;
        };

        double resultado = montoMxn / tasaCambio;
        double resultadoRedondeado = Math.round(resultado * 100.0) / 100.0;

        return Map.of(
                "montoOriginalMxn", montoMxn,
                "monedaDestino", monedaDestino,
                "tasaAplicada", tasaCambio,
                "montoConvertido", resultadoRedondeado);
    }

    // Motor Financiero del Demo #2 (Programado por Uriel en Pesos Mexicanos - MXN)
    public Map<String, Object> calcularCotizacionDemo(
            String tipoProyecto, int modulosExtra, boolean incluyeBd, boolean urgente) {

        // Precio base en Pesos Mexicanos (MXN) según el tipo de proyecto elegido
        double total = 2500.0; // Valor inicial por defecto: Landing Page ($2,500 MXN)

        if ("onlineStore".equals(tipoProyecto)) {
            total = 7500.0; // Tienda en Línea ($7,500 MXN)
        } else if ("posSystem".equals(tipoProyecto)) {
            total = 9500.0; // Sistema de Inventario y Punto de Venta ($9,500 MXN)
        }

        // Sumamos las pantallas o módulos extra
        total = total + (modulosExtra * 800.0);

        // Si incluye Base de Datos SQL / Cloud
        if (incluyeBd) {
            total = total + 2000.0;
        }

        // Si requiere Entrega Urgente Express, aumentamos un 20% (* 1.20)
        if (urgente) {
            total = total * 1.20;
        }

        return Map.of(
                "tipoProyecto", tipoProyecto,
                "totalEstimado", total,
                "moneda", "MXN");
    }

    // Regla de Negocio + Guardar en Base de Datos SQL (POST)
    public SolicitudContacto guardarSolicitud(SolicitudContacto nuevaSolicitud) {
        if (nuevaSolicitud.getPresupuesto() != null && nuevaSolicitud.getPresupuesto() < 100) {
            throw new IllegalArgumentException("El presupuesto mínimo es de $100 USD");
        }
        return solicitudRepository.save(nuevaSolicitud);
    }

    // Método que pasa cualquier texto por la "licuadora" criptográfica SHA-256 de
    // Java 21
    private String generarHashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytesHash);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar clave");
        }
    }

    // Método blindado por Uriel: Consultar solicitudes en SQL exigiendo Hash
    // SHA-256 + Bloqueo de 15 min tras 3 fallos
    public List<SolicitudContacto> obtenerSolicitudes(String claveIngresada) {
        // PASO A: Revisar si el sistema está dentro de los 15 minutos de castigo
        if (bloqueadoHasta != null && LocalDateTime.now().isBefore(bloqueadoHasta)) {
            throw new IllegalStateException(
                    "Sistema bloqueado temporalmente por seguridad (15 min). Intenta mas tarde.");
        }

        // PASO B: Limpiamos espacios (.trim()) y licuamos la clave para obtener su Hash
        // SHA-256
        String claveLimpia = (claveIngresada != null) ? claveIngresada.trim() : "";
        String hashIngresado = generarHashSHA256(claveLimpia);

        // PASO C: Si el Hash secreto NO es igual al hashIngresado, sumamos 1 intento
        // fallido
        if (!HASH_ADMIN_SECRETO.equals(hashIngresado)) {
            intentosFallidos++;
            if (intentosFallidos >= 3) {
                bloqueadoHasta = LocalDateTime.now().plusMinutes(15);
                intentosFallidos = 0;
                throw new IllegalArgumentException("3 intentos fallidos. Acceso bloqueado por 15 minutos.");
            }
            throw new IllegalArgumentException("Clave incorrecta. Intentos restantes: " + (3 - intentosFallidos));
        }

        // Si acertó la clave, reiniciamos contadores y entregamos los clientes de
        // Supabase:
        intentosFallidos = 0;
        bloqueadoHasta = null;
        return solicitudRepository.findAll();
    }

    /*
     * Cumplimiento Legal (Derechos ARCO / GDPR): Baja de correo electrónico
     * y borrado de datos
     */
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
