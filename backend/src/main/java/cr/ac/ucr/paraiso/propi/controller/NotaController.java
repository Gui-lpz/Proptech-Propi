
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

import cr.ac.ucr.paraiso.propi.business.NotaService;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService service;
    private final JwtTokenProvider jwt;

    /* CONSTRUCTOR */

    public NotaController(
            NotaService service,
            JwtTokenProvider jwt) {

        this.service = service;
        this.jwt = jwt;
    }

    /*
     * CONSULTAR NOTAS
     * 
     * GET /api/notas=
     */

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    /* REGISTRAR NOTA NUEVA */

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody NotaDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = extraerToken(auth);

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar los datos de la nota.");
        }

        // Evitar actualizaciones mediante POST.
        if (dto.getNotaId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Para actualizar una nota utilice PUT.");
        }

        String usuario = jwt.getUsername(token);
        String sesion = jwt.getSessionId(token);

        service.guardar(
                dto,
                usuario,
                sesion);

        return ResponseEntity.ok().build();
    }

    /* ACTUALIZAR NOTA */

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> actualizar(
            @PathVariable("id") Integer notaId,
            @RequestBody NotaDTO dto,
            @RequestHeader("Authorization") String auth) {

        String token = extraerToken(auth);

        if (notaId == null || notaId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de nota no es valido.");
        }

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar los datos de la nota.");
        }

        String usuario = jwt.getUsername(token);
        String sesion = jwt.getSessionId(token);

        // El ID de la ruta tiene prioridad.
        dto.setNotaId(notaId);

        service.actualizar(
                notaId,
                dto,
                usuario,
                sesion);

        return ResponseEntity.ok().build();
    }

    /* ELIMINAR NOTA */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(
            @PathVariable("id") Integer notaId,
            @RequestHeader("Authorization") String auth) {

        String token = extraerToken(auth);

        jwt.getUsername(token);
        jwt.getSessionId(token);

        if (notaId == null || notaId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de nota no es valido.");
        }

        service.eliminar(notaId);

        return ResponseEntity.noContent().build();
    }

    /*
     * EXTRAER TOKEN BEARER
     */

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
