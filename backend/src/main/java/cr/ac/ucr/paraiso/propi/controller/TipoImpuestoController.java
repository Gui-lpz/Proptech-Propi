package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cr.ac.ucr.paraiso.propi.business.TipoImpuestoService;
import cr.ac.ucr.paraiso.propi.dto.TipoImpuestoDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/impuestos")
public class TipoImpuestoController {

    private final TipoImpuestoService service;
    private final JwtTokenProvider jwt;

    public TipoImpuestoController(
            TipoImpuestoService service,
            JwtTokenProvider jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<TipoImpuestoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody TipoImpuestoDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = auth.substring(7);
        service.guardar(
                dto,
                jwt.getUsername(token),
                jwt.getSessionId(token));

        return ResponseEntity.ok().build();
    }
}
