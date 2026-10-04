package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cr.ac.ucr.paraiso.propi.business.FacturaService;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService service;
    private final JwtTokenProvider jwt;

    public FacturaController(
            FacturaService service,
            JwtTokenProvider jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestBody FacturaRequestDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = auth.substring(7);

        Integer id = service.crear(
                dto,
                jwt.getUsername(token),
                jwt.getSessionId(token));

        return ResponseEntity.ok(
                Map.of("facturaId", id));
    }
}
