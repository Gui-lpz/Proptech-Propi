package cr.ac.ucr.paraiso.propi.data;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CatalogoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    /* PROVINCIAS */

    public List<Map<String, Object>> listarProvincias() {

        String sql = """
                SELECT
                    ProvinciaID AS provinciaId,
                    CodigoProvincia AS codigoProvincia,
                    Nombre AS nombre
                FROM Provincia
                ORDER BY ProvinciaID
                """;

        return jdbcTemplate
                .queryForList(sql);
    }

    /* CANTONES POR PROVINCIA */

    public List<Map<String, Object>> listarCantones(
            int provinciaId) {

        String sql = """
                SELECT
                    CantonID AS cantonId,
                    ProvinciaID AS provinciaId,
                    CodigoCanton AS codigoCanton,
                    Nombre AS nombre
                FROM Canton
                WHERE ProvinciaID = ?
                ORDER BY CantonID
                """;

        return jdbcTemplate
                .queryForList(
                        sql,
                        provinciaId);
    }

    /* DISTRITOS POR CANTÓN */

    public List<Map<String, Object>> listarDistritos(
            int cantonId) {

        String sql = """
                SELECT
                    DistritoID AS distritoId,
                    CantonID AS cantonId,
                    CodigoDistrito AS codigoDistrito,
                    Nombre AS nombre
                FROM Distrito
                WHERE CantonID = ?
                ORDER BY DistritoID
                """;

        return jdbcTemplate
                .queryForList(
                        sql,
                        cantonId);
    }

    /* ACTIVIDADES ECONÓMICAS */

    public List<Map<String, Object>> listarActividadesEconomicas() {

        String sql = """
                SELECT
                    ActividadEconomicaID
                        AS actividadEconomicaId,

                    CodigoActividad
                        AS codigoActividad,

                    Descripcion
                        AS descripcion

                FROM ActividadEconomica

                WHERE Estado = 'A'

                ORDER BY CodigoActividad
                """;

        return jdbcTemplate
                .queryForList(sql);
    }

    /* CABYS */

    public List<Map<String, Object>> listarCabys() {

        String sql = """
                SELECT
                    CabysID AS cabysId,
                    CodigoCABYS AS codigoCABYS,
                    Descripcion AS descripcion

                FROM Cabys

                WHERE Estado = 'A'

                ORDER BY CodigoCABYS
                """;

        return jdbcTemplate
                .queryForList(sql);
    }
}