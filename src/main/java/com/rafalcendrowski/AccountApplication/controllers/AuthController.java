package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.security.JwtService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserRegisterDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Log4j2
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public UserDto addAccount(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        if (userService.hasUser(userRegisterDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        var user = new User(userRegisterDto.getEmail(), passwordEncoder.encode(userRegisterDto.getPassword()),
                userRegisterDto.getName(), userRegisterDto.getLastname());
        userService.registerUser(user);
        return UserDto.of(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var userDetails = userDetailsService.loadUserByUsername(request.username());

        if (!userDetails.getPassword().equals(passwordEncoder.encode(request.password()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/changepass")
    public UserDto changePassword(@RequestBody @Valid Password newPassword, @AuthenticationPrincipal User user) {
        if (passwordEncoder.matches(newPassword.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must be different");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
            userService.updateUser(user);
            return UserDto.of(user);
        }
    }

}

