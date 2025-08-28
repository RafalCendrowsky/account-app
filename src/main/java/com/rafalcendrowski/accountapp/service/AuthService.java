package com.rafalcendrowski.accountapp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rafalcendrowski.accountapp.api.auth.request.LoginRequest;
import com.rafalcendrowski.accountapp.api.auth.response.LoginResponse;
import com.rafalcendrowski.accountapp.exceptions.LoginException;
import com.rafalcendrowski.accountapp.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {
    private final UserService userService;
    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm algorithm;

    public AuthService(
            UserService userService,
            SecurityProperties securityProperties,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.securityProperties = securityProperties;
        this.passwordEncoder = passwordEncoder;
        this.algorithm = Algorithm.HMAC256(securityProperties.getJwt().secret());
    }

    public LoginResponse authenticate(LoginRequest request) {
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(request.username());
        } catch (UsernameNotFoundException e) {
            log.warn("Invalid username: {}", request.username());
            throw new LoginException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            log.warn("Invalid password for user: {}", request.username());
            throw new LoginException("Invalid credentials");
        }

        var token = generateToken(userDetails);
        return new LoginResponse(token);
    }

    public String generateToken(UserDetails userDetails) {
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("roles", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + securityProperties.getJwt().expiration()))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        try {
            var verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            return null;
        } catch (JWTVerificationException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }
}
