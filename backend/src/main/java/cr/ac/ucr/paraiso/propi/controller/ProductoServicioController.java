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

import cr.ac.ucr.paraiso.propi.business.ProductoServicioService;
import cr.ac.ucr.paraiso.propi.dto.ProductoServicioDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/productos")
public class ProductoServicioController {

    private final ProductoServicioService service;

    private final JwtTokenProvider jwt;

    public ProductoServicioController(
            ProductoServicioService service,
            JwtTokenProvider jwt) {

        this.service = service;

        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<ProductoServicioDTO>> listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    @PostMapping
    public ResponseEntity<Void> crear(
            @RequestBody ProductoServicioDTO dto,

            @RequestHeader("Authorization") String authorization) {

        guardar(
                dto,
                authorization);

        return ResponseEntity
                .ok()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable Integer id,

            @RequestBody ProductoServicioDTO dto,

            @RequestHeader("Authorization") String authorization) {

        dto.setProductoServicioId(id);

        guardar(
                dto,
                authorization);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id,

            @RequestHeader("Authorization") String authorization) {

        String token = authorization.substring(7);

        service.eliminar(
                id,
                jwt.getUsername(token),
                jwt.getSessionId(token));

        return ResponseEntity
                .noContent()
                .build();
    }

    private void guardar(
            ProductoServicioDTO dto,
            String authorization) {

        String token = authorization.substring(7);

        service.guardar(
                dto,
                jwt.getUsername(token),
                jwt.getSessionId(token));
    }
}
