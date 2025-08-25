package com.rafalcendrowski.AccountApplication.security;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Logger secLogger;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(13);
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        .requestMatchers("/api/payments/*").hasRole("ACCOUNTANT")
                        .requestMatchers("/api/payments/user/*").hasRole("ACCOUNTANT")
                        .requestMatchers(HttpMethod.GET, "/api/empl/**").hasAnyRole("USER", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.GET, "/api/security/events").hasRole("AUDITOR")
                        .anyRequest().hasRole("ADMINISTRATOR")
                )
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new RestAuthenticationEntryPoint(secLogger)))
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}

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

