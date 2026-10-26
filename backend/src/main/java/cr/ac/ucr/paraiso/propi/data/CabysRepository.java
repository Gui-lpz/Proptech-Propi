package cr.ac.ucr.paraiso.propi.data;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.CabysDTO;

@Repository
public class CabysRepository {
        private final JdbcTemplate jdbc;

        public CabysRepository(JdbcTemplate jdbc) {
                this.jdbc = jdbc;
        }

        public List<CabysDTO> listar() {
                return jdbc.query("EXEC dbo.sp_Cabys_Listar", (rs, n) -> {
                        CabysDTO dto = new CabysDTO();
                        dto.setCabysId(rs.getInt("CabysID"));
                        dto.setCodigoCABYS(rs.getString("CodigoCABYS"));
                        dto.setDescripcion(rs.getString("Descripcion"));
                        dto.setEstado(rs.getString("Estado"));
                        return dto;
                });
        }

        public void guardar(CabysDTO dto, String usuario, String sesion) {
                jdbc.queryForObject("EXEC dbo.sp_Cabys_Guardar ?, ?, ?, ?, ?, ?", Integer.class,
                                dto.getCabysId(), dto.getCodigoCABYS(), dto.getDescripcion(),
                                dto.getEstado(), usuario, sesion);
        }

        public void eliminar(Integer cabysId, String usuario, String sesion) {
                jdbc.update("EXEC dbo.sp_Cabys_Eliminar ?, ?, ?", cabysId, usuario, sesion);
        }
}
