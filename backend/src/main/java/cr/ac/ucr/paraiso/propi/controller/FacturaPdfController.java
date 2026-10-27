
package cr.ac.ucr.paraiso.propi.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.propi.business.FacturaPdfService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaPdfController {

        private final FacturaPdfService pdfService;

        public FacturaPdfController(FacturaPdfService pdfService) {
                this.pdfService = pdfService;
        }

        @GetMapping("/{id}/pdf")
        public ResponseEntity<byte[]> descargarPdf(
                        @PathVariable Integer id,
                        Authentication authentication) throws IOException {

                if (authentication == null
                                || authentication.getAuthorities() == null) {

                        return ResponseEntity
                                        .status(HttpStatus.FORBIDDEN)
                                        .build();
                }

                boolean autorizado = authentication.getAuthorities()
                                .stream()
                                .anyMatch(autoridad -> autoridad.getAuthority()
                                                .equals("ROLE_ADMINISTRADOR")
                                                || autoridad.getAuthority()
                                                                .equals("ROLE_DIGITADOR"));

                if (!autorizado) {
                        return ResponseEntity
                                        .status(HttpStatus.FORBIDDEN)
                                        .build();
                }

                if (id == null || id <= 0) {
                        return ResponseEntity
                                        .badRequest()
                                        .build();
                }

                byte[] pdf;

                try {
                        pdf = pdfService.generar(id);

                } catch (Exception e) {

                        System.err.println(
                                        "ERROR AL GENERAR PDF DE FACTURA " + id);

                        e.printStackTrace();

                        throw new IllegalStateException(
                                        "No fue posible generar el PDF de la factura " + id,
                                        e);
                }

                String nombreArchivo = String.format("Factura_Propi_%08d.pdf", id);

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\""
                                                                + nombreArchivo + "\"")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdf);
        }
}
