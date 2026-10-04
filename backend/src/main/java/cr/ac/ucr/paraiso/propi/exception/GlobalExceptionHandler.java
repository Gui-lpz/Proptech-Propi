package cr.ac.ucr.paraiso.propi.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Map<String, Object>> accesoDenegado(
                        AccessDeniedException ex) {

                return respuesta(
                                HttpStatus.FORBIDDEN,
                                "No tiene permisos para realizar esta operación.");
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> general(
                        Exception ex) {

                return respuesta(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ex.getMessage());
        }

        private ResponseEntity<Map<String, Object>> respuesta(
                        HttpStatus status,
                        String message) {

                Map<String, Object> body = new LinkedHashMap<>();

                body.put("timestamp", LocalDateTime.now());
                body.put("status", status.value());
                body.put("message", message);

                return ResponseEntity
                                .status(status)
                                .body(body);
        }
}
