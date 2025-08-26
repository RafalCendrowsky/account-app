package com.rafalcendrowski.accountapp.api.auth;

import com.rafalcendrowski.accountapp.api.auth.request.LoginRequest;
import com.rafalcendrowski.accountapp.security.JwtComponent;
import com.rafalcendrowski.accountapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtComponent jwtComponent;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var userDetails = userService.loadUserByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var token = jwtComponent.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

}

