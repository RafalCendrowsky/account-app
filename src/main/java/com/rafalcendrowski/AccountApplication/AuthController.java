package com.rafalcendrowski.AccountApplication;

import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
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
@Log4j2
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Logger secLogger;

    @PostMapping("/signup")
    public Map<String, Object> addAccount(@Valid @RequestBody UserBody userBody, @AuthenticationPrincipal User authUser) {
        if (userRepository.findByUsername(userBody.getEmail().toLowerCase()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        } else if (isBreached(userBody.getPassword())) {
            throw new BreachedPasswordException();
        }
        User user = new User(userBody.getEmail().toLowerCase(Locale.ROOT), passwordEncoder.encode(userBody.getPassword()),
                userBody.getName(), userBody.getLastname());
        if (userRepository.count() == 0) {
            user.setRoles(Set.of(User.Role.ADMINISTRATOR));
        }
        userRepository.save(user);
        String subject = authUser == null ? "Anonymous" : authUser.getName();
        secLogger.info(LoggerConfig.getEventLogMap(subject, user.getUsername(), "CREATE_USER", "api/auth/signup"));
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
            secLogger.info(LoggerConfig.getEventLogMap(user.getName(), user.getUsername(), "CHANGE_PASSWORD", "api/auth/changepass"));
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

@Data
class Password {
    @NotEmpty
    @Size(min=12)
    String password;
}

@Data
@NoArgsConstructor
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
}
