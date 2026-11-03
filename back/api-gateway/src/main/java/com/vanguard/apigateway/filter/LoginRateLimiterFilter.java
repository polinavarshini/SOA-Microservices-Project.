package com.vanguard.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A lightweight, in-memory rate limiter to protect /api/auth/login.
 * Suitable for a student project/demo without requiring Redis infrastructure.
 */
@Component
public class LoginRateLimiterFilter implements GlobalFilter, Ordered {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // Config: 5 requests per minute per IP for login
    private static final int MAX_TOKENS = 5;
    private static final long REFILL_DURATION_MS = Duration.ofMinutes(1).toMillis();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if ("/api/auth/login".equals(path)) {
            String clientIp = getClientIp(exchange);
            TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(MAX_TOKENS, REFILL_DURATION_MS));

            if (!bucket.tryConsume()) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        }
        
        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            if (exchange.getRequest().getRemoteAddress() != null) {
                ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            } else {
                ip = "unknown";
            }
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return -50; // Run after JWT filter, before routing
    }

    private static class TokenBucket {
        private final int maxTokens;
        private final long refillMs;
        private int currentTokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, long refillMs) {
            this.maxTokens = maxTokens;
            this.refillMs = refillMs;
            this.currentTokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (currentTokens > 0) {
                currentTokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            if (now - lastRefillTime >= refillMs) {
                currentTokens = maxTokens;
                lastRefillTime = now;
            }
        }
    }
}
