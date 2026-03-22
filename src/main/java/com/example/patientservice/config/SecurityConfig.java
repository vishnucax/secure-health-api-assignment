package com.example.patientservice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 🔐 Convert Keycloak roles → Spring roles
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess != null) {
                Object rolesObj = realmAccess.get("roles");

                if (rolesObj instanceof List<?> roles) {
                    for (Object role : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
                    }
                }
            }

            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    // 🔥 FIX: Custom JWT Decoder (solves 401 issue)
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(
                "http://localhost:9090/realms/healthcare/protocol/openid-connect/certs"
        ).build();

        // ✅ Validate ONLY issuer (ignore audience problem)
        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer("http://localhost:9090/realms/healthcare");

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    // 🔐 Security rules (RBAC)
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/patients/test").permitAll()
            .requestMatchers(HttpMethod.GET, "/patients/**")
                .hasAnyRole("viewer", "editor")
            .requestMatchers(HttpMethod.POST, "/patients")
                .hasRole("editor")
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .decoder(jwtDecoder()) // 🔥 IMPORTANT
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        );

    return http.build();
}
}