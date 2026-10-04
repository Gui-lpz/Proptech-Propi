package cr.ac.ucr.paraiso.propi.business;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.data.UsuarioPruebaRepository;
import cr.ac.ucr.paraiso.propi.dto.AuthRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.AuthResponseDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@Service
public class AuthService {

    private final UsuarioPruebaRepository usuarioRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UsuarioPruebaRepository usuarioRepository,
            JwtTokenProvider jwtTokenProvider) {

        this.usuarioRepository = usuarioRepository;

        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponseDTO login(
            AuthRequestDTO request,
            String terminal,
            String ip) {

        Map<String, Object> usuario = usuarioRepository.buscarUsuario(
                request.getUsername(),
                request.getPassword());

        if (usuario == null) {

            throw new RuntimeException(
                    "Usuario o contraseña incorrectos.");
        }

        String rol = usuario.get("Rol")
                .toString();

        List<String> roles = List.of(rol);

        String sessionId = UUID.randomUUID()
                .toString();

        String token = jwtTokenProvider.generateToken(
                request.getUsername(),
                roles,
                sessionId);

        return new AuthResponseDTO(
                token,
                request.getUsername(),
                roles,
                jwtTokenProvider
                        .getExpirationTime(token),
                sessionId);
    }
}