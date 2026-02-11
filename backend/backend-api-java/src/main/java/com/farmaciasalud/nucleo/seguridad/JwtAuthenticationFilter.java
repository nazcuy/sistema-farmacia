package com.farmaciasalud.nucleo.seguridad;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Este método se ejecuta en cada request HTTP.
     * Extrae el token JWT del header Authorization y valida la autenticación.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extrae el token del header "Authorization: Bearer <token>"
            String jwt = getJwtFromRequest(request);
            
            // Valida el token y obtiene la autenticación
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Obtiene el ID del usuario del token
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                
                // Carga los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(
                        tokenProvider.getEmailFromToken(jwt));
                
                // Crea el objeto de autenticación
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Establece la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Si falla algo, simplemente no establece autenticación
            // El request continuará sin autenticación
            logger.error("No se pudo establecer la autenticación del usuario", ex);
        }
        
        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrae el token JWT del header Authorization.
     * Formato esperado: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extrae todo después de "Bearer "
        }
        
        return null;
    }
}