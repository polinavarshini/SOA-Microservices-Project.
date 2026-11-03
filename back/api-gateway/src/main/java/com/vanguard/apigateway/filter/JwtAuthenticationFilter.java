package com.vanguard.apigateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Public endpoints that do not require JWT
    private final List<Pattern> publicEndpoints = List.of(
            Pattern.compile("^/api/auth/login$"),
            Pattern.compile("^/api/auth/register$"), // Just in case
            Pattern.compile("^/actuator/.*$")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Check if route is public
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange); // Allow
        }

        // 2. Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rejectRequest(exchange.getResponse());
        }

        String token = authHeader.substring(7);

        // 3. Validate Token Signature & Expiration
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token);
        } catch (JwtException e) {
            return rejectRequest(exchange.getResponse());
        }

        // Token is valid. We preserve the original Authorization header unmodified 
        // to propagate the token downstream to the microservices.
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        if (path == null) return false;
        return path.contains("/api/auth/login") || 
               path.contains("/api/auth/register") || 
               path.contains("/actuator");
    }

    private Mono<Void> rejectRequest(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // Run early, right after Correlation ID
    }
}
