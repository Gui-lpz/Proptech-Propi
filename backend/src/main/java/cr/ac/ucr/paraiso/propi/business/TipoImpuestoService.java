package cr.ac.ucr.paraiso.propi.business;

import java.util.List;
import org.springframework.stereotype.Service;
import cr.ac.ucr.paraiso.propi.data.TipoImpuestoRepository;
import cr.ac.ucr.paraiso.propi.dto.TipoImpuestoDTO;

@Service
public class TipoImpuestoService {

    private final TipoImpuestoRepository repository;

    public TipoImpuestoService(TipoImpuestoRepository repository) {
        this.repository = repository;
    }

    public List<TipoImpuestoDTO> listar() {
        return repository.listar();
    }

    public void guardar(
            TipoImpuestoDTO dto,
            String usuario,
            String sesion) {
        repository.guardar(dto, usuario, sesion);
    }
}
