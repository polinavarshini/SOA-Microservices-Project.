package com.vanguard.settlementservice.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class InstanceIdentifierFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:UNKNOWN}")
    private String appName;

    @Value("${server.port:8080}")
    private String port;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.addHeader("X-Served-By", appName + ":" + port);
        filterChain.doFilter(request, response);
    }
}
