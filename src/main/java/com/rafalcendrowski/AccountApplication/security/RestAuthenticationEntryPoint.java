package com.rafalcendrowski.AccountApplication.security;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

record RestAuthenticationEntryPoint(Logger secLogger) implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String subject = request.getUserPrincipal() == null ? "Anonymous" : request.getUserPrincipal().getName();
        String path = request.getRequestURI();
        if (!path.equals("/error")) { // without this every login_failed event would have duplicate logs
            secLogger.info(LoggerConfig.getEventLogMap(subject, path, "LOGIN_FAILED", path));
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
