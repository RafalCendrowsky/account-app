package com.rafalcendrowski.AccountApplication.security;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain filterChain
    ) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var decodedJWT = jwtService.verifyToken(token);
                var username = decodedJWT.getSubject();
                var roles = decodedJWT.getClaim("roles").asList(String.class)
                        .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                var authToken = new UsernamePasswordAuthenticationToken(username, null, roles);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        if (filterChain != null)
            filterChain.doFilter(request, response);
    }
}
