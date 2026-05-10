package com.fuerzapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Value("${frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // Rutas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/pagos/webhook").permitAll()

                // Solo administrador de plataforma
                .requestMatchers("/api/admin/**").hasRole("ADMIN_PLATAFORMA")

                // Propietario y admin
                .requestMatchers(HttpMethod.POST, "/api/gimnasios/*/entrenadores").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers(HttpMethod.DELETE, "/api/gimnasios/*/entrenadores/*").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers(HttpMethod.POST, "/api/gimnasios/*/clientes").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers(HttpMethod.DELETE, "/api/gimnasios/*/clientes/*").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers(HttpMethod.GET, "/api/gimnasios/*/suscripciones").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/gimnasios/*/extras").authenticated()
                .requestMatchers("/api/gimnasios/*/suscripciones/**").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers("/api/gimnasios/*/extras/**").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")
                .requestMatchers("/api/gimnasios/*/pagos").hasAnyRole("PROPIETARIO", "ADMIN_PLATAFORMA")

                // Entrenador y propietario
                .requestMatchers(HttpMethod.POST, "/api/gimnasios/*/entrenamientos").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers(HttpMethod.PUT, "/api/entrenamientos/*").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers(HttpMethod.DELETE, "/api/entrenamientos/*").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers("/api/entrenamientos/*/ejercicios/**").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers("/api/entrenamientos/*/sesiones/**").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers("/api/entrenamientos/*/asignar-cliente").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers("/api/sesiones/*/asignar-cliente").hasAnyRole("ENTRENADOR", "PROPIETARIO")
                .requestMatchers(HttpMethod.POST, "/api/gimnasios/*/ejercicios").hasAnyRole("ENTRENADOR", "PROPIETARIO")

                // Cliente
                .requestMatchers("/api/pagos/crear-sesion").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/sesiones/*/inscripcion").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/sesiones/*/inscripcion").hasRole("CLIENTE")

                // Cualquier usuario autenticado
                .requestMatchers(HttpMethod.GET, "/api/ejercicios/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/clientes/*/entrenamientos").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/clientes/*/suscripcion").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/clientes/*/pagos").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/pagos/*/factura").authenticated()

                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
