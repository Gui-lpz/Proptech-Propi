package cr.ac.ucr.paraiso.propi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catálogos disponibles para formularios: devuelve IDs reales de la base de
 * datos.
 */
@RestController
@RequestMapping("/api/catalogos-extra")
public class CatalogosExtraController {
    private final JdbcTemplate jdbc;

    public CatalogosExtraController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/tipos-identificacion")
    public List<Map<String, Object>> tipos() {
        return jdbc.queryForList(
                "SELECT TipoIdentificacionID AS tipoIdentificacionId, Codigo AS codigo, Descripcion AS descripcion FROM dbo.TipoIdentificacion WHERE Estado='A' ORDER BY Codigo");
    }

    @GetMapping("/unidades-medida")
    public List<Map<String, Object>> unidades() {
        return jdbc.queryForList(
                "SELECT UnidadMedidaID AS unidadMedidaId, Codigo AS codigo, Descripcion AS descripcion FROM dbo.UnidadMedida WHERE Estado='A' ORDER BY Codigo");
    }

    @GetMapping("/condiciones-venta")
    public List<Map<String, Object>> condiciones() {
        return jdbc.queryForList(
                "SELECT CondicionVentaID AS condicionVentaId, Codigo AS codigo, Descripcion AS descripcion FROM dbo.CondicionVenta ORDER BY Codigo");
    }

    @GetMapping("/medios-pago")
    public List<Map<String, Object>> medios() {
        return jdbc.queryForList(
                "SELECT MedioPagoID AS medioPagoId, Codigo AS codigo, Descripcion AS descripcion FROM dbo.MedioPago ORDER BY Codigo");
    }

    @GetMapping("/monedas")
    public List<Map<String, Object>> monedas() {
        return jdbc.queryForList(
                "SELECT MonedaID AS monedaId, Codigo AS codigo, Descripcion AS descripcion FROM dbo.Moneda ORDER BY Codigo");
    }
}
