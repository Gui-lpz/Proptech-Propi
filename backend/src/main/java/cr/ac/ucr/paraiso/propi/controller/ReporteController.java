package cr.ac.ucr.paraiso.propi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cr.ac.ucr.paraiso.propi.business.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> clientes(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            @RequestParam(defaultValue = "TODOS") String estado) {

        return ResponseEntity.ok(
                service.clientes(desde, hasta, estado));
    }

    @GetMapping("/facturas")
    public ResponseEntity<List<Map<String, Object>>> facturas(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        return ResponseEntity.ok(
                service.facturas(desde, hasta));
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<Map<String, Object>>> auditoria(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            @RequestParam(defaultValue = "TODOS") String usuario) {

        return ResponseEntity.ok(
                service.auditoria(desde, hasta, usuario));
    }

    @GetMapping("/seguridad")
    public ResponseEntity<List<Map<String, Object>>> seguridad(
            @RequestParam(defaultValue = "TODOS") String usuario) {

        return ResponseEntity.ok(
                service.seguridad(usuario));
    }
}
