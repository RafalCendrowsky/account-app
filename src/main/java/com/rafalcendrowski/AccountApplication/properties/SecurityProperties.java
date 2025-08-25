package com.rafalcendrowski.AccountApplication.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private final Jwt jwt;

    public record Jwt(String secret, long expiration) {
    }
}
