package com.vanguard.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            request = request.mutate().header(CORRELATION_ID_HEADER, correlationId).build();
            exchange = exchange.mutate().request(request).build();
        }
        
        final ServerWebExchange finalExchange = exchange;
        final String finalCorrelationId = correlationId;
        
        finalExchange.getResponse().beforeCommit(() -> {
            finalExchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
            return Mono.empty();
        });
        
        return chain.filter(finalExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
