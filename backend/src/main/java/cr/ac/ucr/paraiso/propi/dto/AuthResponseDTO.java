package cr.ac.ucr.paraiso.propi.dto;

import java.util.List;

public class AuthResponseDTO {

    private String token;
    private String username;
    private List<String> roles;
    private long expirationTime;
    private String sessionId;

    public AuthResponseDTO(
            String token,
            String username,
            List<String> roles,
            long expirationTime,
            String sessionId) {

        this.token = token;
        this.username = username;
        this.roles = roles;
        this.expirationTime = expirationTime;
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getSessionId() {
        return sessionId;
    }
}
