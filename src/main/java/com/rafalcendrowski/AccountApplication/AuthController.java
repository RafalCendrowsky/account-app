package com.rafalcendrowski.AccountApplication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid email")
class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super();
    }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/signup")
    public Map<String, String> addAccount(@Valid @RequestBody Account account) {
        String[] email = account.getEmail().split("@");
        String domain = email.length == 2 ? email[1] : "";
        if (domain.equals("acme.com")) {
                return Map.of("name", account.getName(), "lastname", account.getLastname(),
                            "email", account.getEmail());
        } else {
            throw new InvalidEmailException();
        }
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
