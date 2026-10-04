package cr.ac.ucr.paraiso.propi.data;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioPruebaRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioPruebaRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> buscarUsuario(
            String username,
            String password) {

        String sql = """
                SELECT
                    UsuarioID,
                    Username,
                    PasswordHash,
                    Rol,
                    Activo
                FROM dbo.UsuarioPrueba
                WHERE Username = ?
                  AND PasswordHash = ?
                  AND Activo = 1
                """;

        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                sql,
                username,
                password);

        if (resultado.isEmpty()) {
            return null;
        }

        return resultado.get(0);
    }
}