package cr.ac.ucr.paraiso.propi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioActual {

    public String username() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return "DESCONOCIDO";
        }

        return authentication.getName();
    }
}
