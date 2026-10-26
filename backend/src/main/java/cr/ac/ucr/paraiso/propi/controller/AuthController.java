
package cr.ac.ucr.paraiso.propi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.propi.business.AuthService;
import cr.ac.ucr.paraiso.propi.dto.AuthRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.AuthResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthService authService;

        // Constructor
        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        /*
         * REQ 003: INICIAR SESIÓN
         *
         * Recibe usuario y contraseña del frontend.
         * AuthService los valida mediante SQL Server.
         */
        @PostMapping("/login")
        public ResponseEntity<AuthResponseDTO> login(
                        @Valid @RequestBody AuthRequestDTO request,
                        HttpServletRequest servletRequest) {

                String terminal = servletRequest.getHeader("User-Agent");
                String ip = servletRequest.getRemoteAddr();

                AuthResponseDTO respuesta = authService.login(
                                request,
                                terminal,
                                ip);

                return ResponseEntity.ok(respuesta);
        }

        /*
         * REQ 003: CERRAR SESIÓN
         *
         * Recibe el identificador de la sesión.
         * El usuario se obtiene de Spring Security.
         */
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(
                        @RequestBody LogoutRequest request,
                        Authentication authentication) {

                if (authentication == null
                                || !authentication.isAuthenticated()
                                || request == null
                                || request.sessionId() == null) {

                        return ResponseEntity.status(401).build();
                }

                authService.cerrarSesion(
                                authentication.getName(),
                                request.sessionId());

                return ResponseEntity.noContent().build();
        }

        /*
         * DTO pequeño para recibir sessionId
         * desde JavaScript.
         */
        public record LogoutRequest(String sessionId) {
        }
}
