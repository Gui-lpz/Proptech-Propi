package cr.ac.ucr.paraiso.propi.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReporteRepository(
            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> clientes(
            LocalDate desde,
            LocalDate hasta,
            String estado) {

        return jdbcTemplate.queryForList(
                "EXEC reportes.sp_ReporteClientes ?, ?, ?",
                desde,
                hasta,
                estado);
    }

    public List<Map<String, Object>> facturas(
            LocalDate desde,
            LocalDate hasta) {

        return jdbcTemplate.queryForList(
                "EXEC reportes.sp_ReporteFacturas ?, ?",
                desde,
                hasta);
    }

    public List<Map<String, Object>> auditoria(
            LocalDate desde,
            LocalDate hasta,
            String usuario) {

        return jdbcTemplate.queryForList(
                "EXEC reportes.sp_ReporteAuditoria ?, ?, ?",
                desde,
                hasta,
                usuario);
    }

    public List<Map<String, Object>> seguridad(
            String usuario) {

        return jdbcTemplate.queryForList(
                "EXEC reportes.sp_ReporteSeguridad ?",
                usuario);
    }
}
