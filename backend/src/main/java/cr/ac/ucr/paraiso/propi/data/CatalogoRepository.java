package cr.ac.ucr.paraiso.propi.data;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CatalogoRepository {
    private final JdbcTemplate jdbc;

    public CatalogoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listarProvincias() {
        return jdbc.queryForList(
                "SELECT ProvinciaID AS provinciaId, CodigoProvincia AS codigoProvincia, Nombre AS nombre FROM dbo.Provincia WHERE Estado = 'A' ORDER BY Nombre");
    }

    public List<Map<String, Object>> listarCantones(int provinciaId) {
        return jdbc.queryForList(
                "SELECT CantonID AS cantonId, ProvinciaID AS provinciaId, CodigoCanton AS codigoCanton, Nombre AS nombre FROM dbo.Canton WHERE Estado='A' AND ProvinciaID=? ORDER BY Nombre",
                provinciaId);
    }

    public List<Map<String, Object>> listarDistritos(int cantonId) {
        return jdbc.queryForList(
                "SELECT DistritoID AS distritoId, CantonID AS cantonId, CodigoDistrito AS codigoDistrito, Nombre AS nombre FROM dbo.Distrito WHERE Estado='A' AND CantonID=? ORDER BY Nombre",
                cantonId);
    }

    public List<Map<String, Object>> listarActividadesEconomicas() {
        return jdbc.queryForList(
                "SELECT ActividadEconomicaID AS actividadEconomicaId, Codigo AS codigoActividad, Descripcion AS descripcion FROM dbo.ActividadEconomica WHERE Estado='A' ORDER BY Codigo");
    }

    public List<Map<String, Object>> listarCabys() {
        return jdbc.queryForList(
                "SELECT CabysID AS cabysId, CodigoCABYS AS codigoCABYS, Descripcion AS descripcion FROM dbo.Cabys WHERE Estado='A' ORDER BY CodigoCABYS");
    }
}
