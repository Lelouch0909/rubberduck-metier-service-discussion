package com.lontsi.rubberduckmetierservicediscussion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Récupérer le token à partir de l'en-tête Authorization
        ServerHttpRequest request = exchange.getRequest();
        try {
            String principal = request.getHeaders().getFirst("X-User-Principal");
            String authoritiesHeader = request.getHeaders().getFirst("X-User-Authorities");
            String name = request.getHeaders().getFirst("X-User-Name");
            String credentials = request.getHeaders().getFirst("X-User-Credentials");
            log.warn("Auth header detected: principal={}, authorities={}", principal, authoritiesHeader);


            if (principal == null) {
                // Pas d'auth info, on continue sans auth
                return chain.filter(exchange);
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            if (authoritiesHeader != null) {
                authorities = Arrays.stream(authoritiesHeader.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);

            // Associer le contexte de sécurité à la requête
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(requeste -> requeste.headers(headers -> headers.add("X-Auth-Context", "true")))
                    .build();

            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


        return null;
    }


}
