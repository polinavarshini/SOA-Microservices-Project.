package com.vanguard.apigateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "eureka.client.enabled=false",
    "spring.cloud.gateway.routes[0].id=auth-service",
    "spring.cloud.gateway.routes[0].uri=http://localhost:${wiremock.server.port}",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/api/auth/**",
    "spring.cloud.gateway.routes[1].id=policy-service",
    "spring.cloud.gateway.routes[1].uri=http://localhost:${wiremock.server.port}",
    "spring.cloud.gateway.routes[1].predicates[0]=Path=/api/policies/**"
})
@AutoConfigureWireMock(port = 0)
public class GatewayTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testPublicRoute_AllowedWithoutJwt() {
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse().withStatus(200).withBody("Login OK")));

        webClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Login OK");
    }

    @Test
    public void testProtectedRoute_MissingJwt_Returns401() {
        webClient.get().uri("/api/policies/123")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testProtectedRoute_InvalidJwt_Returns401() {
        webClient.get().uri("/api/policies/123")
                .header("Authorization", "Bearer invalid.token.here")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testCorrelationId_GeneratedIfMissing() {
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse().withStatus(200)));

        webClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Correlation-ID");
    }

    @Test
    public void testRateLimiting_LoginRoute() {
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse().withStatus(200)));

        for (int i = 0; i < 5; i++) {
            webClient.post().uri("/api/auth/login").exchange().expectStatus().isOk();
        }
        
        webClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().isEqualTo(429);
    }
}
