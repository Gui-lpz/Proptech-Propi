package cr.ac.ucr.paraiso.propi.business;

import java.util.List;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.ClienteRepository;
import cr.ac.ucr.paraiso.propi.dto.ClienteDTO;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(
            ClienteRepository repository) {

        this.repository = repository;
    }

    public List<ClienteDTO> listar() {

        return repository.listar();
    }

    public ClienteDTO guardar(
            ClienteDTO dto,
            String usuario,
            String sesion) {

        return repository.guardar(
                dto,
                usuario,
                sesion);
    }

    public void eliminar(
            Integer clienteId,
            String usuario,
            String sesion) {

        repository.eliminar(
                clienteId,
                usuario,
                sesion);
    }
}
