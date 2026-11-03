package com.vanguard.apigateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";

        if (ex instanceof ResponseStatusException) {
            status = (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
            message = status.getReasonPhrase();
        }

        // Do not expose stack traces or downstream Java exceptions.
        if (status == HttpStatus.SERVICE_UNAVAILABLE || status == HttpStatus.BAD_GATEWAY) {
            message = "Downstream Service Unavailable";
        }

        String correlationId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        }

        String jsonResponse = String.format(
            "{\"error\": \"%s\", \"status\": %d, \"correlationId\": \"%s\"}",
            message, status.value(), correlationId != null ? correlationId : ""
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
