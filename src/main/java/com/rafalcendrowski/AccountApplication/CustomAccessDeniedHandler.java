package com.rafalcendrowski.AccountApplication;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    Logger secLogger;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String subject = request.getUserPrincipal() == null ? "Anonymous" : request.getUserPrincipal().getName();
        secLogger.warn(LoggerConfig.getEventLogMap(subject, request.getServletPath(),
                "ACCESS_DENIED", request.getServletPath()));
        response.sendError(403, "Access Denied!");
    }
}
