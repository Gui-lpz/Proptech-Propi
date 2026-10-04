package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.propi.business.CatalogoService;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

        private final CatalogoService service;

        public CatalogoController(
                        CatalogoService service) {

                this.service = service;
        }

        /* PROVINCIAS */

        @GetMapping("/provincias")
        public ResponseEntity<List<Map<String, Object>>> provincias() {

                return ResponseEntity.ok(
                                service.listarProvincias());
        }

        /* CANTONES */

        @GetMapping("/cantones")
        public ResponseEntity<List<Map<String, Object>>> cantones(
                        @RequestParam Integer provinciaId) {

                return ResponseEntity.ok(
                                service.listarCantones(
                                                provinciaId));
        }

        /* DISTRITOS */

        @GetMapping("/distritos")
        public ResponseEntity<List<Map<String, Object>>> distritos(
                        @RequestParam Integer cantonId) {

                return ResponseEntity.ok(
                                service.listarDistritos(
                                                cantonId));
        }

        /* ACTIVIDADES ECONÓMICAS */

        @GetMapping("/actividades-economicas")
        public ResponseEntity<List<Map<String, Object>>> actividadesEconomicas() {

                return ResponseEntity.ok(
                                service
                                                .listarActividadesEconomicas());
        }

        /* CABYS */

        @GetMapping("/cabys")
        public ResponseEntity<List<Map<String, Object>>> cabys() {

                return ResponseEntity.ok(
                                service.listarCabys());
        }
}