
package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import cr.ac.ucr.paraiso.propi.business.FacturaService;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService service;
    private final JwtTokenProvider jwt;

    /* CONSTRUCTOR= */

    public FacturaController(
            FacturaService service,
            JwtTokenProvider jwt) {

        this.service = service;
        this.jwt = jwt;
    }

    /* CONSULTAR FACTURAS */

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    /* REGISTRAR FACTURA Y SUS DETALLES */

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestBody FacturaRequestDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = extraerToken(auth);

        String usuario = jwt.getUsername(token);
        String sesion = jwt.getSessionId(token);

        Integer facturaId = service.crear(
                dto,
                usuario,
                sesion);

        return ResponseEntity.ok(
                Map.of("facturaId", facturaId));
    }

    /* ACTUALIZAR FACTURA */

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> actualizar(
            @PathVariable("id") Integer facturaId,
            @RequestBody FacturaRequestDTO dto,
            @RequestHeader("Authorization") String auth) {

        // Obtener token JWT.
        String token = extraerToken(auth);

        // Obtener usuario autenticado.
        String usuario = jwt.getUsername(token);

        // Obtener identificador de sesion.
        String sesion = jwt.getSessionId(token);

        // Validar identificador de factura.
        if (facturaId == null || facturaId <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de factura no es valido.");
        }

        // Validar datos recibidos.
        if (dto == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar los datos de la factura.");
        }

        // Actualizar factura mediante el servicio.
        // Este metodo debe implementarse en FacturaService.
        service.actualizar(
                facturaId,
                dto,
                usuario,
                sesion);

        return ResponseEntity.noContent().build();
    }

    /* ELIMINAR FACTURA */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(
            @PathVariable("id") Integer facturaId,
            @RequestHeader("Authorization") String auth) {

        // Verificar formato del encabezado.
        String token = extraerToken(auth);

        // Obtener identidad desde JWT.
        jwt.getUsername(token);
        jwt.getSessionId(token);

        // Validar identificador.
        if (facturaId == null || facturaId <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de factura no es valido.");
        }

        // Eliminar mediante el servicio.
        service.eliminar(facturaId);

        return ResponseEntity.noContent().build();
    }

    /* EXTRAER TOKEN BEARER= */

    private String extraerToken(String auth) {

        if (auth == null ||
                !auth.startsWith("Bearer ") ||
                auth.substring(7).isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token de autenticacion no proporcionado.");
        }

        return auth.substring(7).trim();
    }
}
