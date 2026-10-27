
package cr.ac.ucr.paraiso.propi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.propi.business.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    // REPORTE DE CLIENTES
    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> clientes(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

            @RequestParam(defaultValue = "TODOS") String estado) {

        return ResponseEntity.ok(
                service.clientes(desde, hasta, estado));
    }

    // REPORTE DE FACTURAS
    @GetMapping("/facturas")
    public ResponseEntity<List<Map<String, Object>>> facturas(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return ResponseEntity.ok(
                service.facturas(desde, hasta));
    }

    // REPORTE DE AUDITORIA
    @GetMapping("/auditoria")
    public ResponseEntity<List<Map<String, Object>>> auditoria(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

            @RequestParam(defaultValue = "TODOS") String usuario) {

        return ResponseEntity.ok(
                service.auditoria(desde, hasta, usuario));
    }

    // REPORTE DE SEGURIDAD
    @GetMapping("/seguridad")
    public ResponseEntity<List<Map<String, Object>>> seguridad(

            @RequestParam(defaultValue = "TODOS") String usuario) {

        return ResponseEntity.ok(
                service.seguridad(usuario));
    }
}
