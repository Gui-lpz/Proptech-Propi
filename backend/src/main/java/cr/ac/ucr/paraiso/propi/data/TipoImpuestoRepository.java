package cr.ac.ucr.paraiso.propi.data;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.TipoImpuestoDTO;

@Repository
public class TipoImpuestoRepository {
    private final JdbcTemplate jdbc;

    public TipoImpuestoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TipoImpuestoDTO> listar() {
        return jdbc.query("EXEC dbo.sp_TipoImpuesto_Listar", (rs, n) -> {
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

    public void guardar(TipoImpuestoDTO dto, String usuario, String sesion) {

        jdbc.queryForList("EXEC dbo.sp_TipoImpuesto_Guardar ?, ?, ?, ?, ?, ?",
                dto.getTipoImpuestoId(), dto.getCodigo(), dto.getDescripcion(),
                dto.getPorcentaje(), dto.getTarifa(), dto.getEstado());
    }

    public void eliminar(Integer id) {
        jdbc.update("EXEC dbo.sp_TipoImpuesto_Eliminar ?", id);
    }
}
