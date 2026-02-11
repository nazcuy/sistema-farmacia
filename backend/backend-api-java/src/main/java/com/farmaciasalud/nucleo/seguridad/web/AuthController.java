package com.farmaciasalud.nucleo.seguridad.web;

/**
 * Nicolás Azcuy - Desarrollador de Software - Linkedin: https://www.linkedin.com/in/nicolas-azcuy-prog/
 */
import com.farmaciasalud.nucleo.seguridad.JwtTokenProvider;
import com.farmaciasalud.nucleo.seguridad.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * POST /api/auth/login
     * Autentica al usuario y retorna un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // Autentica las credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            
            // Establece la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Genera el token JWT
            String token = tokenProvider.generateToken(authentication);
            
            // Obtiene los datos del usuario
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Prepara la respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", userDetails.getId());
            response.put("email", userDetails.getEmail());
            response.put("nombre", userDetails.getNombreCompleto());
            response.put("rol", userDetails.getRol().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }
    
    /**
     * GET /api/auth/me
     * Obtiene los datos del usuario actualmente autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", userDetails.getId());
        response.put("email", userDetails.getEmail());
        response.put("nombre", userDetails.getNombre());
        response.put("apellido", userDetails.getApellido());
        response.put("rol", userDetails.getRol().name());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Record para el request de login.
     */
    public record LoginRequest(String email, String password) {}
}