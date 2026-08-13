package org.dataki.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación por API Key para los endpoints /api/**
 * La clave se configura mediante la propiedad api.key (env var API_KEY).
 * Debe ser igual en todos los entornos de despliegue.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-API-Key";

    private final String apiKey;

    public ApiKeyFilter(@Value("${api.key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            return true;
        }
        String path = request.getRequestURI();
        return !path.startsWith("/api/")
                || path.startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String provided = request.getHeader(HEADER_NAME);
        if (!apiKey.equals(provided)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"API key inválida o ausente\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
