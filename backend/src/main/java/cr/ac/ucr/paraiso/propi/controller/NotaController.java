package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cr.ac.ucr.paraiso.propi.business.NotaService;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService service;
    private final JwtTokenProvider jwt;

    public NotaController(NotaService service, JwtTokenProvider jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody NotaDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = auth.substring(7);

        service.guardar(
                dto,
                jwt.getUsername(token),
                jwt.getSessionId(token));

        return ResponseEntity.ok().build();
    }
}
