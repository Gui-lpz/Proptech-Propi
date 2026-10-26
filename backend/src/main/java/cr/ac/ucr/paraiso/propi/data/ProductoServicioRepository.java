package cr.ac.ucr.paraiso.propi.data;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.ProductoServicioDTO;

@Repository
public class ProductoServicioRepository {
        private final JdbcTemplate jdbc;

        public ProductoServicioRepository(JdbcTemplate jdbc) {
                this.jdbc = jdbc;
        }

        public List<ProductoServicioDTO> listar() {
                return jdbc.query(
                                "SELECT p.*, u.Descripcion AS UnidadMedida FROM dbo.ProductoServicio p JOIN dbo.UnidadMedida u ON u.UnidadMedidaID=p.UnidadMedidaID ORDER BY p.ProductoServicioID",
                                (rs, n) -> {
                                        ProductoServicioDTO dto = new ProductoServicioDTO();
                                        dto.setProductoServicioId(rs.getInt("ProductoServicioID"));
                                        dto.setCodigoCabys(rs.getString("CodigoCABYS"));
                                        dto.setDescripcion(rs.getString("Descripcion"));
                                        dto.setUnidadMedidaId(rs.getInt("UnidadMedidaID"));
                                        dto.setUnidadMedida(rs.getString("UnidadMedida"));
                                        dto.setPrecio(rs.getBigDecimal("Precio"));
                                        dto.setTipoImpuestoId(rs.getInt("TipoImpuestoID"));
                                        dto.setEstado(rs.getString("Estado"));
                                        return dto;
                                });
        }

        public void guardar(ProductoServicioDTO dto, String usuario, String sesion) {
                if (dto.getUnidadMedidaId() == null)
                        throw new IllegalArgumentException("Falta unidadMedidaId");
                jdbc.queryForList("EXEC dbo.sp_ProductoServicio_Guardar ?, ?, ?, ?, ?, ?, ?",
                                dto.getProductoServicioId(), dto.getCodigoCabys(), dto.getDescripcion(),
                                dto.getUnidadMedidaId(), dto.getPrecio(), dto.getTipoImpuestoId(), dto.getEstado());
        }

        public void eliminar(Integer productoServicioId, String usuario, String sesion) {
                jdbc.update("EXEC dbo.sp_ProductoServicio_Eliminar ?", productoServicioId);
        }
}
