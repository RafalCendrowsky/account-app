package com.rafalcendrowski.AccountApplication;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password has been breached")
class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException() { super(); }
}


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public Map<String, Object> addAccount(@Valid @RequestBody Account account) {
        if (userRepository.findByUsername(account.getEmail().toLowerCase()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        } else if (isBreached(account.getPassword())) {
            throw new BreachedPasswordException();
        }
        User user = new User();
        user.setUsername(account.getEmail().toLowerCase());
        user.setName(account.getName());
        user.setLastName(account.getLastname());
        user.setPassword(passwordEncoder.encode(account.getPassword()));
        userRepository.save(user);
        return user.getUserMap();
    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(@RequestBody @Valid @NotBlank @Size(min=12) String new_password, @AuthenticationPrincipal User user) {
        if(isBreached(new_password)) {
            throw new BreachedPasswordException();
        } else if(passwordEncoder.matches(new_password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must be different");
        } else {
            user.setPassword(passwordEncoder.encode(new_password));
            return Map.of("email", user.getUsername(), "status", "Password has been updated successfully");
        }
    }

    public boolean isBreached(String password) {
        return List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
                "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
                "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember",
                "PasswordForDecember").contains(password);
    }

}


class Account {
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastname;
    @NotNull
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String email;
    @NotEmpty
    @Size(min=12)
    private String password;

    public Account(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
