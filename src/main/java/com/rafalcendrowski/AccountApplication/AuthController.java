package com.rafalcendrowski.AccountApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


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
    public Map<String, Object> addAccount(@Valid @RequestBody UserBody userBody) {
        if (userRepository.findByUsername(userBody.getEmail().toLowerCase()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        } else if (isBreached(userBody.getPassword())) {
            throw new BreachedPasswordException();
        }
        User user = new User(userBody.getEmail().toLowerCase(Locale.ROOT), passwordEncoder.encode(userBody.getPassword()),
                userBody.getName(), userBody.getLastname());
        if (userRepository.count() == 0) {
            user.setRoles(Set.of(new Role("ROLE_ADMIN")));
        }
        userRepository.save(user);
        return user.getUserMap();
    }


    @PostMapping("/changepass")
    public Map<String, String> changePassword(@RequestBody @Valid Password newPassword, @AuthenticationPrincipal User user) {
        if(isBreached(newPassword.getPassword())) {
            throw new BreachedPasswordException();
        } else if(passwordEncoder.matches(newPassword.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must be different");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
            userRepository.save(user);
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

class Password {
    @NotEmpty
    @Size(min=12)
    String password;

    public Password() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class UserBody {
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

    public UserBody() {}

    public UserBody(String name, String lastname, String email, String password) {
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
