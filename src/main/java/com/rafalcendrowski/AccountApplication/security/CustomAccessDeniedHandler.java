package com.rafalcendrowski.AccountApplication.security;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    final Logger secLogger;

    @Override
    // a custom handle method to enable logging access denied events
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String subject = request.getUserPrincipal() == null ? "Anonymous" : request.getUserPrincipal().getName();
        secLogger.warn(LoggerConfig.getEventLogMap(subject, request.getServletPath(),
                "ACCESS_DENIED", request.getServletPath()));
        response.sendError(403, "Access Denied!");
    }
}
