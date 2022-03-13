package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import com.rafalcendrowski.AccountApplication.models.UserModelAssembler;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserRegisterDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password has been breached")
class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException() { super(); }
}


@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Logger secLogger;

    @Autowired
    private UserModelAssembler userModelAssembler;

    // a placeholder for a database of breached passwords
    private final Set<String> breachedPasswords = Set.of("breachedPassword");

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


    @PostMapping("/changepass")
    public EntityModel<UserDto> changePassword(@RequestBody @Valid Password newPassword, @AuthenticationPrincipal User user) {
        if(isBreached(newPassword.getPassword())) {
            throw new BreachedPasswordException();
        } else if(passwordEncoder.matches(newPassword.getPassword(), user.getPassword())) {
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

@Data
class Password { // a wrapper object for validation purposes
    @NotEmpty
    @Size(min=12)
    String password;
}