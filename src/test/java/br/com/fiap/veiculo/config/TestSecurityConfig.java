package br.com.fiap.veiculo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable()); // desativa CSRF
        return http.build();
    }
}
@TestConfiguration
class JwtDecoderTestConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> null; // apenas um mock simples
    }
}

@TestConfiguration
class OAuth2ClientTestConfig {

    @Bean
    public org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clientRegistrationRepository() {
        // Cria um ClientRegistration fake
        org.springframework.security.oauth2.client.registration.ClientRegistration registration =
                org.springframework.security.oauth2.client.registration.ClientRegistration.withRegistrationId("cognito")
                        .clientId("fake-client-id")
                        .clientSecret("fake-client-secret")
                        .scope("openid")
                        .authorizationUri("http://fake-issuer/oauth2/authorize")
                        .tokenUri("http://fake-issuer/oauth2/token")
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                        .build();

        return new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(registration);
    }
}
