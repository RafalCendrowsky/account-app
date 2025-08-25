package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.exceptions.BreachedPasswordException;
import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import com.rafalcendrowski.AccountApplication.models.UserModelAssembler;
import com.rafalcendrowski.AccountApplication.security.JwtService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserRegisterDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.EntityModel;
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
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
@Log4j2
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Logger secLogger;
    private final UserModelAssembler userModelAssembler;

    // a placeholder for a database of breached passwords
    private final Set<String> breachedPasswords = Set.of("breachedPassword");
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public EntityModel<UserDto> addAccount(@Valid @RequestBody UserRegisterDto userRegisterDto, @AuthenticationPrincipal User authUser) {
        if (userService.hasUser(userRegisterDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        } else if (isBreached(userRegisterDto.getPassword())) {
            throw new BreachedPasswordException();
        }
        User user = new User(userRegisterDto.getEmail(), passwordEncoder.encode(userRegisterDto.getPassword()),
                userRegisterDto.getName(), userRegisterDto.getLastname());
        userService.registerUser(user);
        // the subject/perpetrator of the logging event
        String subject = authUser == null ? "Anonymous" : authUser.getName();
        secLogger.info(LoggerConfig.getEventLogMap(subject, user.getUsername(), "CREATE_USER", "api/auth/signup"));
        return userModelAssembler.toModel(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var userDetails = userDetailsService.loadUserByUsername(request.username());

        // validate password manually (BCrypt, etc.)
        if (!userDetails.getPassword().equals(passwordEncoder.encode(request.password()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/changepass")
    public EntityModel<UserDto> changePassword(@RequestBody @Valid Password newPassword, @AuthenticationPrincipal User user) {
        if (isBreached(newPassword.getPassword())) {
            throw new BreachedPasswordException();
        } else if (passwordEncoder.matches(newPassword.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must be different");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
            userService.updateUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(user.getName(), user.getUsername(), "CHANGE_PASSWORD", "api/auth/changepass"));
            return userModelAssembler.toModel(user);
        }
    }

    public boolean isBreached(String password) {
        return breachedPasswords.contains(password);
    }

}

