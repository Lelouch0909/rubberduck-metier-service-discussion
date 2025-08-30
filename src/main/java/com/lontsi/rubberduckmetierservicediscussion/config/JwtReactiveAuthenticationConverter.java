package com.lontsi.rubberduckmetierservicediscussion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JwtReactiveAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return Mono.fromCallable(() -> {
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

            String principal = jwt.getClaimAsString("preferred_username");
            if (principal == null) {
                principal = jwt.getSubject();
            }

            log.info("JWT Authentication - User: {}, Authorities: {}",
                    principal,
                    authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(", ")));

            return new JwtAuthenticationToken(jwt, authorities, principal);
        });
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1. Extraire les scopes standards (scp ou scope)
        Collection<String> scopes = jwt.getClaimAsStringList("scope");
        if (scopes == null) {
            scopes = jwt.getClaimAsStringList("scp");
        }
        if (scopes != null) {
            authorities.addAll(scopes.stream()
                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                    .collect(Collectors.toSet()));
        }

        // 2. Extraire les realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles != null) {
                authorities.addAll(roles.stream()
                        .filter(role -> !role.startsWith("default-roles-")) // Filtrer les rôles par défaut
                        .map(role -> {
                            // Nettoyer les doubles préfixes ROLE_
                            String cleanRole = role.startsWith("ROLE_") ? role.substring(5) : role;
                            return new SimpleGrantedAuthority("ROLE_" + cleanRole.toUpperCase());
                        })
                        .collect(Collectors.toSet()));
            }
        }

        // 3. Extraire les client roles (optionnel)
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            // Pour chaque client
            resourceAccess.forEach((clientId, clientData) -> {
                if (clientData instanceof Map) {
                    Map<String, Object> clientMap = (Map<String, Object>) clientData;
                    Collection<String> clientRoles = (Collection<String>) clientMap.get("roles");
                    if (clientRoles != null) {
                        authorities.addAll(clientRoles.stream()
                                .map(role -> new SimpleGrantedAuthority("CLIENT_" + clientId.toUpperCase() + "_" + role.toUpperCase()))
                                .collect(Collectors.toSet()));
                    }
                }
            });
        }

        log.debug("Extracted authorities: {}", authorities);
        return authorities;
    }
}