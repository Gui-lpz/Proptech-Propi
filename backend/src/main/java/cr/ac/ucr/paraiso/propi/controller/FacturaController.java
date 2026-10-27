
package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // READ: listar facturas.
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    // CREATE: registrar factura y detalles.
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestBody FacturaRequestDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = auth.substring(7);

        String usuario = jwt.getUsername(token);
        String sesion = jwt.getSessionId(token);

        Integer facturaId = service.crear(
                dto,
                usuario,
                sesion);

        return ResponseEntity.ok(
                Map.of("facturaId", facturaId));
    }
}
