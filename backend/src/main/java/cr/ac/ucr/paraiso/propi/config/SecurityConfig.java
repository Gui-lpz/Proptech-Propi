
package cr.ac.ucr.paraiso.propi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cr.ac.ucr.paraiso.propi.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        // Constructor
        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        /*
         * REQ 003 Y REQ 004: SEGURIDAD DE LA APLICACION
         *
         * REQ 003:
         * - Permitir iniciar sesion.
         * - Proteger el cierre de sesion.
         * - Validar las solicitudes mediante JWT.
         *
         * REQ 004:
         * - Administrador: acceso completo.
         * - Digitador: clientes, facturas y notas.
         * - Consulta: lectura y reportes.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                // Permitir comunicacion con el frontend
                                .cors(Customizer.withDefaults())

                                // API que utiliza JWT en Authorization
                                .csrf(csrf -> csrf.disable())

                                // No crear sesiones HTTP en el servidor
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                // Configurar permisos de cada ruta
                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * SOLICITUDES OPTIONS
                                                 * Necesarias para CORS.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                /*
                                                 * REQ 003: LOGIN
                                                 * No requiere autenticacion previa.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/login")
                                                .permitAll()

                                                /*
                                                 * REQ 003: LOGOUT
                                                 * Requiere autenticacion con JWT.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/logout")
                                                .authenticated()

                                                /*
                                                 * CATALOGOS
                                                 * Los tres roles pueden consultar.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/catalogos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * CLIENTES: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * CLIENTES: CREAR
                                                 * Administrador y Digitador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /*
                                                 * CLIENTES: MODIFICAR
                                                 * Administrador y Digitador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /*
                                                 * CLIENTES: ELIMINAR
                                                 * Solamente Administrador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/clientes/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * CABYS: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/cabys/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * CABYS: CREAR
                                                 * Solamente Administrador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/cabys/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * CABYS: MODIFICAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/cabys/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * CABYS: ELIMINAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/cabys/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * PRODUCTOS Y SERVICIOS: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/productos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * PRODUCTOS Y SERVICIOS: CREAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/productos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * PRODUCTOS Y SERVICIOS: MODIFICAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/productos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * PRODUCTOS Y SERVICIOS: ELIMINAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/productos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * IMPUESTOS: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/impuestos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * IMPUESTOS: CREAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/impuestos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * IMPUESTOS: MODIFICAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/impuestos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * IMPUESTOS: ELIMINAR
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/impuestos/**")
                                                .hasRole("ADMINISTRADOR")

                                                /*
                                                 * FACTURAS: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/facturas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * FACTURAS: CREAR
                                                 * Administrador y Digitador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/facturas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /*
                                                 * NOTAS DE CREDITO Y DEBITO: CONSULTAR
                                                 * Los tres roles pueden leer.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/notas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /*
                                                 * NOTAS DE CREDITO Y DEBITO: CREAR
                                                 * Administrador y Digitador.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/notas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /*
                                                 * REPORTES
                                                 * Administrador y Consulta.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/reportes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "CONSULTA")

                                                /*
                                                 * OTRAS RUTAS
                                                 * Requieren al menos autenticacion.
                                                 */
                                                .anyRequest()
                                                .authenticated())

                                /*
                                 * FILTRO JWT
                                 * Valida los tokens antes del filtro
                                 * tradicional de usuario y contraseña.
                                 */
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /*
         * CONFIGURACION CORS
         *
         * Permite que el frontend de Live Server
         * se comunique con Spring Boot en 8080.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                // Direcciones permitidas del frontend
                config.setAllowedOrigins(
                                List.of(
                                                "http://127.0.0.1:5500",
                                                "http://localhost:5500"));

                // Metodos HTTP permitidos
                config.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "PATCH",
                                                "DELETE",
                                                "OPTIONS"));

                // Encabezados permitidos
                config.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type"));

                // Encabezados visibles para el navegador
                config.setExposedHeaders(
                                List.of("Authorization"));

                // No utiliza cookies para autenticacion
                config.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                config);

                return source;
        }
}
