
package cr.ac.ucr.paraiso.propi.business;

import java.util.List;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.CabysRepository;
import cr.ac.ucr.paraiso.propi.dto.CabysDTO;

@Service
public class CabysService {

    private final CabysRepository repository;

    /*
     * Constructor:
     * Recibe el repositorio que se comunica
     * con SQL Server.
     */
    public CabysService(CabysRepository repository) {
        this.repository = repository;
    }

    /*
     * REQ 002: CONSULTAR CABYS
     *
     * Obtiene todos los codigos CABYS
     * registrados en la base de datos.
     */
    public List<CabysDTO> listar() {
        return repository.listar();
    }

    /*
     * REQ 002: CREAR Y ACTUALIZAR CABYS
     *
     * Si cabysId es NULL, crea un registro.
     * Si contiene un ID, actualiza el registro.
     *
     * Usuario y sesion se utilizan
     * para registrar la auditoria.
     */
    public void guardar(
            CabysDTO dto,
            String usuario,
            String sesion) {

        repository.guardar(dto, usuario, sesion);
    }

    /*
     * REQ 002: ELIMINAR CABYS
     *
     * Recibe el ID del registro que
     * se desea desactivar.
     */
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
