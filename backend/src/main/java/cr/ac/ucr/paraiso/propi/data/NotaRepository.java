package cr.ac.ucr.paraiso.propi.data;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;

@Repository
public class NotaRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> listar() {
        return jdbcTemplate.queryForList(
                "EXEC facturacion.sp_Nota_Listar");
    }

    public void guardar(
            NotaDTO dto,
            String usuario,
            String sesion) {

        jdbcTemplate.update(
                "EXEC facturacion.sp_Nota_Guardar ?,?,?,?,?,?,?",
                dto.getNotaId(),
                dto.getCodigo(),
                dto.getFacturaId(),
                dto.getTipo(),
                dto.getMotivo(),
                usuario,
                sesion);
    }
}
