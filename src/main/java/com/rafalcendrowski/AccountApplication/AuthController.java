package com.rafalcendrowski.AccountApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;
import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid email")
class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super();
    }
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User already exists")
class UserExistsException extends RuntimeException {
    public UserExistsException() { super(); }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public Map<String, String> addAccount(@Valid @RequestBody Account account) {
        String[] email = account.getEmail().split("@");
        String domain = email.length == 2 ? email[1] : "";
        if (!domain.equals("acme.com")) {
            throw new InvalidEmailException();
        } else if (userRepository.findByUsername(account.getEmail().toLowerCase()) != null) {
            throw new UserExistsException();
        }
        User user = new User();
        user.setUsername(account.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(account.getPassword()));
        userRepository.save(user);
        return Map.of("name", account.getName(), "lastname", account.getLastname(),
                "email", account.getEmail(), "id", user.getId().toString());
    }
}


class Account {
    @NotNull
    @Size(min=1)
    private String name;
    @NotNull
    @Size(min=1)
    private String lastname;
    @NotNull
    @Size(min=1)
    private String email;
    @NotNull
    @Size(min=1)
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
