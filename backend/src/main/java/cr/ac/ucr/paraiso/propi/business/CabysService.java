package cr.ac.ucr.paraiso.propi.business;

import java.util.List;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.CabysRepository;
import cr.ac.ucr.paraiso.propi.dto.CabysDTO;

@Service
public class CabysService {

    private final CabysRepository repository;

    public CabysService(
            CabysRepository repository) {

        this.repository =
                repository;
    }

    public List<CabysDTO> listar() {

        return repository.listar();
    }

    public void guardar(
            CabysDTO dto,
            String usuario,
            String sesion) {

        repository.guardar(
            dto,
            usuario,
            sesion);
    }

    public void eliminar(
            Integer cabysId,
            String usuario,
            String sesion) {

        repository.eliminar(
            cabysId,
            usuario,
            sesion);
    }
}
