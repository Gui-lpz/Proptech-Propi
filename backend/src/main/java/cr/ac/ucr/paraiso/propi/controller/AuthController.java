package cr.ac.ucr.paraiso.propi.controller;

import org.springframework.http.ResponseEntity;
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

        public AuthController(
                        AuthService authService) {

                this.authService = authService;
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponseDTO> login(
                        @Valid @RequestBody AuthRequestDTO request,
                        HttpServletRequest servletRequest) {

                String terminal = servletRequest.getHeader(
                                "User-Agent");

                String ip = servletRequest.getRemoteAddr();

                return ResponseEntity.ok(
                                authService.login(
                                                request,
                                                terminal,
                                                ip));
        }
}
