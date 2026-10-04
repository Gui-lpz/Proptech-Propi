package cr.ac.ucr.paraiso.propi.data;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.ProductoServicioDTO;

@Repository
public class ProductoServicioRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductoServicioRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductoServicioDTO> listar() {

        return jdbcTemplate.query(
                "EXEC facturacion.sp_Producto_Listar",
                (rs, rowNum) -> {

                    ProductoServicioDTO dto = new ProductoServicioDTO();

                    dto.setProductoServicioId(
                            rs.getInt(
                                    "ProductoServicioID"));

                    dto.setCabysId(
                            rs.getInt(
                                    "CabysID"));

                    dto.setCodigoCabys(
                            rs.getString(
                                    "CodigoCABYS"));

                    dto.setDescripcion(
                            rs.getString(
                                    "Descripcion"));

                    dto.setUnidadMedida(
                            rs.getString(
                                    "UnidadMedida"));

                    dto.setPrecio(
                            rs.getBigDecimal(
                                    "Precio"));

                    dto.setTipoImpuestoId(
                            rs.getInt(
                                    "TipoImpuestoID"));

                    dto.setEstado(
                            rs.getString(
                                    "Estado"));

                    return dto;
                });
    }

    public void guardar(
            ProductoServicioDTO dto,
            String usuario,
            String sesion) {

        jdbcTemplate.update(
                """
                        EXEC facturacion.sp_Producto_Guardar
                            ?, ?, ?, ?, ?, ?, ?, ?, ?
                        """,
                dto.getProductoServicioId(),
                dto.getCabysId(),
                dto.getDescripcion(),
                dto.getUnidadMedida(),
                dto.getPrecio(),
                dto.getTipoImpuestoId(),
                dto.getEstado(),
                usuario,
                sesion);
    }

    public void eliminar(
            Integer productoServicioId,
            String usuario,
            String sesion) {

        jdbcTemplate.update(
                """
                        EXEC facturacion.sp_Producto_Eliminar
                            ?, ?, ?
                        """,
                productoServicioId,
                usuario,
                sesion);
    }
}
