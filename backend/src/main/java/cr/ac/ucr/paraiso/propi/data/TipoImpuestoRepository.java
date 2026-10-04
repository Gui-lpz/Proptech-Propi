package cr.ac.ucr.paraiso.propi.data;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.TipoImpuestoDTO;

@Repository
public class TipoImpuestoRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoImpuestoRepository(
            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TipoImpuestoDTO> listar() {
        return jdbcTemplate.query(
                "EXEC facturacion.sp_TipoImpuesto_Listar",
                (rs, rowNum) -> {
                    TipoImpuestoDTO dto = new TipoImpuestoDTO();
                    dto.setTipoImpuestoId(rs.getInt("TipoImpuestoID"));
                    dto.setCodigo(rs.getString("Codigo"));
                    dto.setDescripcion(rs.getString("Descripcion"));
                    dto.setPorcentaje(rs.getBigDecimal("Porcentaje"));
                    dto.setTarifa(rs.getBigDecimal("Tarifa"));
                    dto.setEstado(rs.getString("Estado"));
                    return dto;
                });
    }

    public void guardar(
            TipoImpuestoDTO dto,
            String usuario,
            String sesion) {

        jdbcTemplate.update(
                "EXEC facturacion.sp_TipoImpuesto_Guardar ?,?,?,?,?,?,?,?",
                dto.getTipoImpuestoId(),
                dto.getCodigo(),
                dto.getDescripcion(),
                dto.getPorcentaje(),
                dto.getTarifa(),
                dto.getEstado(),
                usuario,
                sesion);
    }
}
