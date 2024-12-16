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
import java.util.Map;

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
        String clientHeader = headers.getFirst("Client");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (clientHeader == null || !clientHeader.equals("2be4986e4bf057b65a0bb9fad7b0df44")) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        return webClientBuilder.build()
                .post()
                .uri("http://auth-service:8083/api/v1/auth/user/verifyToken")
                .bodyValue(Map.of("token", token))
                .retrieve()
                .toEntity(Map.class)
                .flatMap(response -> {
                    Map<String, Object> body = response.getBody();
                    if (body != null && Boolean.TRUE.equals(body.get("response"))) {
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

}

