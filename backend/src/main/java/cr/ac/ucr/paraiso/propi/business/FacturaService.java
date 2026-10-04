package cr.ac.ucr.paraiso.propi.business;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import cr.ac.ucr.paraiso.propi.data.FacturaRepository;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;

@Service
public class FacturaService {

    private final FacturaRepository repository;

    public FacturaService(FacturaRepository repository) {
        this.repository = repository;
    }

    public Integer crear(
            FacturaRequestDTO dto,
            String usuario,
            String sesion) {
        return repository.crear(dto, usuario, sesion);
    }

    public List<Map<String, Object>> listar() {
        return repository.listar();
    }
}
