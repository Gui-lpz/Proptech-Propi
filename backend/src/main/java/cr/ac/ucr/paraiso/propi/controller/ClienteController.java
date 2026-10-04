package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.propi.business.ClienteService;
import cr.ac.ucr.paraiso.propi.dto.ClienteDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

        private final ClienteService service;

        private final JwtTokenProvider jwtTokenProvider;

        public ClienteController(
                        ClienteService service,
                        JwtTokenProvider jwtTokenProvider) {

                this.service = service;

                this.jwtTokenProvider = jwtTokenProvider;
        }

        @GetMapping
        public ResponseEntity<List<ClienteDTO>> listar() {

                return ResponseEntity.ok(
                                service.listar());
        }

        @PostMapping
        public ResponseEntity<ClienteDTO> crear(
                        @RequestBody ClienteDTO dto,

                        @RequestHeader("Authorization") String authorization) {

                return ResponseEntity.ok(
                                guardar(
                                                dto,
                                                authorization));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ClienteDTO> actualizar(
                        @PathVariable Integer id,

                        @RequestBody ClienteDTO dto,

                        @RequestHeader("Authorization") String authorization) {

                dto.setClienteId(id);

                return ResponseEntity.ok(
                                guardar(
                                                dto,
                                                authorization));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(
                        @PathVariable Integer id,

                        @RequestHeader("Authorization") String authorization) {

                String token = authorization.substring(7);

                service.eliminar(
                                id,
                                jwtTokenProvider
                                                .getUsername(token),
                                jwtTokenProvider
                                                .getSessionId(token));

                return ResponseEntity
                                .noContent()
                                .build();
        }

        private ClienteDTO guardar(
                        ClienteDTO dto,
                        String authorization) {

                String token = authorization.substring(7);

                return service.guardar(
                                dto,
                                jwtTokenProvider
                                                .getUsername(token),
                                jwtTokenProvider
                                                .getSessionId(token));
        }
}
