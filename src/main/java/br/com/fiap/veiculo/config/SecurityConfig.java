package br.com.fiap.veiculo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CognitoJwtAuthenticationConverter cognitoJwtAuthConverter;

    public SecurityConfig(CognitoJwtAuthenticationConverter cognitoJwtAuthConverter) {
        this.cognitoJwtAuthConverter = cognitoJwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/configuration/ui",
                                "/configuration/security",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/veiculo/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/veiculo").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/veiculo/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/veiculo/**").hasAnyAuthority("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(cognitoJwtAuthConverter)
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}