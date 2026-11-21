package com.portafolio.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Filtro Global de Seguridad HTTP (Resuelve hallazgos OWASP ZAP: CSP, Clickjacking, MIME-Sniffing y Cache)
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Content-Security-Policy (CSP): Controla exactamente qué scripts, estilos y fuentes puede cargar el navegador
        // y bloquea cualquier intento de incrustar tu sitio dentro de un iframe malicioso (frame-ancestors 'none')
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'; " +
                "form-action 'self'; " +
                "object-src 'none'; " +
                "base-uri 'self';");

        // 2. Anti-Clickjacking: Impide que otros sitios metan a RoliCode dentro de un <iframe> oculto
        response.setHeader("X-Frame-Options", "DENY");

        // 3. Anti-MIME-Sniffing: Obliga al navegador a respetar estrictamente el tipo de archivo declarado
        response.setHeader("X-Content-Type-Options", "nosniff");

        // 4. HSTS y Referrer-Policy: Fuerza HTTPS y evita filtrar URLs privadas en enlaces salientes
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

        // 5. Control de Caché estricto para APIs y rutas sensibles (evita filtración en proxies compartidos)
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/") || uri.contains("admin")) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
        } else {
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
        }

        filterChain.doFilter(request, response);
    }
}
