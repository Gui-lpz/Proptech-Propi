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
                "EXEC dbo.sp_NotasCreditoDebito_Listar");
    }
public void guardar(NotaDTO dto) {
        String sql = "EXEC dbo.sp_NotasCreditoDebito_Guardar @NotaID=?, @Codigo=?, @Fecha=?, @FacturaID=?, @Tipo=?, @Motivo=?, @Monto=?";
        
        // Usamos queryForObject para capturar el SELECT @NotaID final del procedimiento
        Integer notaIdGenerado = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                dto.getNotaId(),
                dto.getCodigo(),
                dto.getFecha(),
                dto.getFacturaId(),
                dto.getTipo(),
                dto.getMotivo(),
                dto.getMonto()
        );
        
    
        dto.setNotaId(notaIdGenerado);
    }
      //esto es para la generación delcodigo consecutivo en cada nota
    public long obtenerSiguienteSecuencial(String tipo) {
    String sql = "SELECT ISNULL(MAX(CAST(RIGHT(Codigo, 10) AS BIGINT)), 0) + 1 " +
                 "FROM dbo.NotasCreditoDebito WHERE Tipo = ?";
    
    Long siguiente = jdbcTemplate.queryForObject(sql, Long.class, tipo);
    return siguiente != null ? siguiente : 1L;
}
    /*
    public void guardar(
            NotaDTO dto,
            String usuario,
            String sesion) {

        jdbcTemplate.update(
                "EXEC dbo.sp_NotasCreditoDebito_Guardar ?,?,?,?,?,?,?",
                dto.getNotaId(),
                dto.getCodigo(),
                dto.getFacturaId(),
                dto.getTipo(),
                dto.getMotivo(),
                usuario,
                sesion);
    }*/
      public List<Map<String, Object>> eliminar() {
        return jdbcTemplate.queryForList(
                "EXEC dbo.sp_NotasCreditoDebito_Eliminar");
    }
}
