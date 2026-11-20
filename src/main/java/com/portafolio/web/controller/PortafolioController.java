package com.portafolio.web.controller;

import com.portafolio.web.model.ProyectoDemo;
import com.portafolio.web.model.Servicio;
import com.portafolio.web.model.SolicitudContacto;
import com.portafolio.web.service.PortafolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

// @RestController: Le dice a Spring que esta clase es el "Mesero" (Controller)
@RestController
// @RequestMapping: Ruta base para todas las peticiones de este controlador
@RequestMapping("/api/portafolio")
public class PortafolioController {

    // El Mesero declara que necesita al Chef (PortafolioService)
    private final PortafolioService portafolioService;

    // Inyección de Dependencias por Constructor: Spring conecta al Chef
    // automáticamente
    public PortafolioController(PortafolioService portafolioService) {
        this.portafolioService = portafolioService;
    }

    // Endpoint 1: Perfil del desarrollador -> GET /api/portafolio/perfil
    @GetMapping("/perfil")
    public Map<String, String> obtenerPerfil() {
        return Map.of(
                "nombre", "Uriel",
                "rol",
                "Somos una empresa desarrolladores comprometidos con mejorar siempre, nos especializamos en desarrollo de aplicaciones web, mobiles y de escritorio",
                "estado", "Disponible",
                "empresa", "RoliCode");
    }

    // Endpoint 2: Catálogo de servicios -> GET /api/portafolio/servicios
    @GetMapping("/servicios")
    public List<Servicio> obtenerServicios() {
        return portafolioService.obtenerCatalogoServicios();
    }

    // Endpoint 3 (Creado por Uriel): Lista de proyectos demo -> GET
    // /api/portafolio/demos
    @GetMapping("/demos")
    public List<ProyectoDemo> obtenerDemos() {
        return portafolioService.obtenerProyectosDemo();
    }

    // Endpoint 4: Recibir formulario de cliente y guardarlo en Base de Datos ->
    // POST /api/portafolio/contacto
    @PostMapping("/contacto")
    public SolicitudContacto crearSolicitud(@RequestBody SolicitudContacto solicitud) {
        return portafolioService.guardarSolicitud(solicitud);
    }

    // Endpoint 5 (Protegido con Hash SHA-256 + Bloqueo 15 min): Consultar
    // solicitudes -> GET /api/portafolio/contacto?clave=...
    @GetMapping("/contacto")
    public ResponseEntity<?> listarSolicitudes(@RequestParam String clave) {
        try {
            List<SolicitudContacto> lista = portafolioService.obtenerSolicitudes(clave);
            return ResponseEntity.ok(lista);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint 6 (Cumplimiento Legal ARCO/GDPR): Baja de correo electrónico ->
    // DELETE /api/portafolio/contacto/baja?email=...
    @DeleteMapping("/contacto/baja")
    public Map<String, Object> solicitarBajaCorreo(@RequestParam String email) {
        long eliminados = portafolioService.darDeBajaCorreo(email);
        return Map.of(
                "mensaje", "Tu correo y datos asociados fueron eliminados exitosamente de nuestra base de datos.",
                "registrosEliminados", eliminados);
    }

    // Endpoint 7 (Demo #2 - Calculadora de Cotizaciones en MXN):
    // GET
    // /api/portafolio/cotizar?tipoProyecto=onlineStore&modulosExtra=2&incluyeBd=true&urgente=false
    @GetMapping("/cotizar")
    public Map<String, Object> cotizarProyecto(
            @RequestParam String tipoProyecto,
            @RequestParam(defaultValue = "0") int modulosExtra,
            @RequestParam(defaultValue = "false") boolean incluyeBd,
            @RequestParam(defaultValue = "false") boolean urgente) {
        return portafolioService.calcularCotizacionDemo(tipoProyecto, modulosExtra, incluyeBd, urgente);
    }

    // Endpoint 8 (Demo #3 - Conversor de Divisas en Java 21):
    // GET /api/portafolio/divisas?montoMxn=5000&monedaDestino=USD
    @GetMapping("/divisas")
    public Map<String, Object> convertirDivisa(
            @RequestParam(defaultValue = "1000") double montoMxn,
            @RequestParam(defaultValue = "USD") String monedaDestino) {
        return portafolioService.convertirDivisaDemo(montoMxn, monedaDestino);
    }
}
