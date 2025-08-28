package com.rafalcendrowski.accountapp.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.rafalcendrowski.accountapp.service.AuthService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain filterChain
    ) throws ServletException, IOException {

        Optional.ofNullable(request)
                .map(req -> req.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> authService.verifyToken(header.substring(7)))
                .map(this::jwtToAuthentication)
                .ifPresentOrElse(
                        authToken -> SecurityContextHolder.getContext().setAuthentication(authToken),
                        SecurityContextHolder::clearContext
                );

        if (filterChain != null)
            filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken jwtToAuthentication(DecodedJWT jwt) {
        var username = jwt.getSubject();
        var roles = jwt.getClaim("roles").asList(String.class)
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, roles);
    }
}
