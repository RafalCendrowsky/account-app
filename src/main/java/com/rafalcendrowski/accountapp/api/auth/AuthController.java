package com.rafalcendrowski.accountapp.api.auth;

import com.rafalcendrowski.accountapp.api.auth.request.LoginRequest;
import com.rafalcendrowski.accountapp.api.auth.response.LoginResponse;
import com.rafalcendrowski.accountapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }

}

