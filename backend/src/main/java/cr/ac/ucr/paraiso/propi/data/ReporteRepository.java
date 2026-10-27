
package cr.ac.ucr.paraiso.propi.data;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteRepository {

        private final JdbcTemplate jdbcTemplate;

        public ReporteRepository(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        // REPORTE DE CLIENTES
        public List<Map<String, Object>> clientes(
                        LocalDate desde,
                        LocalDate hasta,
                        String estado) {

                String filtroEstado = estado == null
                                || estado.isBlank()
                                || estado.equalsIgnoreCase("TODOS")
                                                ? null
                                                : estado;

                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_ReporteClientes ?, ?, ?",
                                Date.valueOf(desde),
                                Date.valueOf(hasta),
                                filtroEstado);
        }

        // REPORTE DE FACTURAS
        public List<Map<String, Object>> facturas(
                        LocalDate desde,
                        LocalDate hasta) {

                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_ReporteFacturas ?, ?",
                                Date.valueOf(desde),
                                Date.valueOf(hasta));
        }

        // REPORTE DE AUDITORIA
        public List<Map<String, Object>> auditoria(
                        LocalDate desde,
                        LocalDate hasta,
                        String usuario) {

                String filtroUsuario = usuario == null
                                || usuario.isBlank()
                                || usuario.equalsIgnoreCase("TODOS")
                                                ? null
                                                : usuario;

                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_ReporteAuditoria ?, ?, ?",
                                Date.valueOf(desde),
                                Date.valueOf(hasta),
                                filtroUsuario);
        }

        // REPORTE DE SEGURIDAD
        public List<Map<String, Object>> seguridad(
                        String usuario) {

                String filtroUsuario = usuario == null
                                || usuario.isBlank()
                                || usuario.equalsIgnoreCase("TODOS")
                                                ? null
                                                : usuario;

                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_ReporteSeguridad ?",
                                filtroUsuario);
        }
}
