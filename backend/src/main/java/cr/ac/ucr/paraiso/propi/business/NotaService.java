package cr.ac.ucr.paraiso.propi.business;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import cr.ac.ucr.paraiso.propi.data.NotaRepository;
import cr.ac.ucr.paraiso.propi.dto.NotaDTO;

@Service
public class NotaService {

    private final NotaRepository repository;

    public NotaService(NotaRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> listar() {
        return repository.listar();
    }

    public void guardar(
            NotaDTO dto,
            String usuario,
            String sesion) {
        repository.guardar(dto, usuario, sesion);
    }
}
