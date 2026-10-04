package cr.ac.ucr.paraiso.propi.business;

import java.util.List;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.ProductoServicioRepository;
import cr.ac.ucr.paraiso.propi.dto.ProductoServicioDTO;

@Service
public class ProductoServicioService {

    private final ProductoServicioRepository repository;

    public ProductoServicioService(
            ProductoServicioRepository repository) {

        this.repository = repository;
    }

    public List<ProductoServicioDTO> listar() {

        return repository.listar();
    }

    public void guardar(
            ProductoServicioDTO dto,
            String usuario,
            String sesion) {

        repository.guardar(
                dto,
                usuario,
                sesion);
    }

    public void eliminar(
            Integer productoServicioId,
            String usuario,
            String sesion) {

        repository.eliminar(
                productoServicioId,
                usuario,
                sesion);
    }
}
