package com.example.api_gateway.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;

    private final List<String> publicEndpoints = List.of("/api/v1/auth/user/login");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (publicEndpoints.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        return webClientBuilder.build()
                .post()
                .uri("http://auth-service:8083/api/v1/auth/verifyToken")
                .bodyValue(token)
                .retrieve()
                .toEntity(Boolean.class)
                .flatMap(response -> {
                    if (Boolean.TRUE.equals(response.getBody())) {
                        //return checkRoleIfNeeded(path, token, exchange, chain);
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<Void> checkRoleIfNeeded(String path, String token, ServerWebExchange exchange, GatewayFilterChain chain) {
        String requiredRole = determineRequiredRole(path);

        if (requiredRole == null) {
            return chain.filter(exchange);
        }

        return webClientBuilder.build()
                .post()
                .uri("http://auth-service:8083/api/v1/auth/role")
                .bodyValue(token)
                .retrieve()
                .toEntity(String.class)
                .flatMap(response -> {
                    if (requiredRole.equalsIgnoreCase(response.getBody())) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    private String determineRequiredRole(String path) {
        if (path.startsWith("/api/v1/admin")) {
            return "ADMIN";
        } else if (path.startsWith("/api/v1/manager")) {
            return "MANAGER";
        }
        return null;
    }
}

