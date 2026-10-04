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

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .cors(Customizer.withDefaults())

                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                /* CORS */
                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                /* LOGIN */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/login")
                                                .permitAll()

                                                /* CATÁLOGOS: lectura para los tres roles */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/catalogos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* CLIENTES: lectura para los tres */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* CLIENTES: crear y modificar admin + digitador */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /* CLIENTES: eliminar solo admin */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/clientes/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                /* CABYS: todos pueden consultar */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/cabys/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* CABYS: CRUD de escritura solo admin */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/cabys/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/cabys/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/cabys/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                /* PRODUCTOS: todos pueden consultar */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/productos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* PRODUCTOS: escritura solo admin */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/productos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/productos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/productos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                /* IMPUESTOS: todos consultan, solo admin modifica */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/impuestos/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/impuestos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/impuestos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/impuestos/**")
                                                .hasRole(
                                                                "ADMINISTRADOR")

                                                /* FACTURAS: lectura para todos */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/facturas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* FACTURAS: creación admin + digitador */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/facturas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /* NOTAS: lectura para todos */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/notas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                /* NOTAS: creación admin + digitador */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/notas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR")

                                                /* REPORTES: visibles para los tres roles */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/reportes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "DIGITADOR",
                                                                "CONSULTA")

                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(
                                List.of(
                                                "http://127.0.0.1:5500",
                                                "http://localhost:5500"));

                config.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "PATCH",
                                                "DELETE",
                                                "OPTIONS"));

                config.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type"));

                config.setExposedHeaders(
                                List.of(
                                                "Authorization"));

                config.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                config);

                return source;
        }
}
