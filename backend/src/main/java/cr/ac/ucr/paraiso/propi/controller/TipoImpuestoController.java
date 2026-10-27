
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

    // READ
    @GetMapping
    public ResponseEntity<List<TipoImpuestoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Void> crear(
            @RequestBody TipoImpuestoDTO dto,
            @RequestHeader("Authorization") String auth) {

        dto.setTipoImpuestoId(null);
        guardarConUsuario(dto, auth);

        return ResponseEntity.ok().build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoImpuestoDTO dto,
            @RequestHeader("Authorization") String auth) {

        dto.setTipoImpuestoId(id);
        guardarConUsuario(dto, auth);

        return ResponseEntity.ok().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    private void guardarConUsuario(
            TipoImpuestoDTO dto,
            String auth) {

        String token = auth.substring(7);

        service.guardar(
                dto,
                jwt.getUsername(token),
                jwt.getSessionId(token));
    }
}
