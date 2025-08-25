package com.rafalcendrowski.AccountApplication.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rafalcendrowski.AccountApplication.properties.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final SecurityProperties securityProperties;
    private final Algorithm algorithm;

    public JwtService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.algorithm = Algorithm.HMAC256(securityProperties.getJwt().secret());
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
        var verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String getUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return verifyToken(token).getClaim("roles").asList(String.class);
    }
}

