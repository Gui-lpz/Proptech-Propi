
package cr.ac.ucr.paraiso.propi.data;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.NotaDTO;

@Repository
public class NotaRepository {

    private final JdbcTemplate jdbcTemplate;

    /* CONSTRUCTOR */

    public NotaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* LISTAR NOTAS DE CREDITO / DEBITO */

    public List<Map<String, Object>> listar() {

        return jdbcTemplate.queryForList(
                "EXEC dbo.sp_NotasCreditoDebito_Listar");
    }

    /*
     * GUARDAR O ACTUALIZAR NOTA
     * 
     * Procedimiento:
     * dbo.sp_NotasCreditoDebito_Guardar
     */

    public void guardar(NotaDTO dto) {

        String sql = "EXEC dbo.sp_NotasCreditoDebito_Guardar " +
                "@NotaID=?, " +
                "@Codigo=?, " +
                "@Fecha=?, " +
                "@FacturaID=?, " +
                "@Tipo=?, " +
                "@CodigoMotivo=?, " +
                "@Motivo=?, " +
                "@Monto=?";

        Integer notaIdGenerado = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                dto.getNotaId(),
                dto.getCodigo(),
                dto.getFecha(),
                dto.getFacturaId(),
                dto.getTipo(),
                dto.getCodigoMotivo(),
                dto.getMotivo(),
                dto.getMonto());

        dto.setNotaId(notaIdGenerado);
    }

    /*
     * 3. OBTENER SIGUIENTE NUMERO SECUENCIAL
     * 
     * Se utiliza para generar el codigo de Hacienda.
     */

    public long obtenerSiguienteSecuencial(String tipo) {

        String sql = "SELECT ISNULL(" +
                "MAX(TRY_CAST(RIGHT(Codigo, 10) AS BIGINT)), " +
                "0) + 1 " +
                "FROM dbo.NotasCreditoDebito " +
                "WHERE Tipo = ?";

        Long siguiente = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                tipo);

        return siguiente != null ? siguiente : 1L;
    }

    /*
     * ELIMINAR NOTA DE CREDITO / DEBITO
     * 
     * Procedimiento:
     * dbo.sp_NotasCreditoDebito_Eliminar
     */

    public void eliminar(Integer notaId) {

        if (notaId == null || notaId <= 0) {

            throw new IllegalArgumentException(
                    "Debe proporcionar un NotaID valido.");
        }

        jdbcTemplate.update(
                "EXEC dbo.sp_NotasCreditoDebito_Eliminar @NotaID = ?",
                notaId);
    }
}
