package com.rafalcendrowski.AccountApplication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @ExceptionHandler
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<String> handleException(NullPointerException exception) {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> addAccount (@Valid @RequestBody Account account) {
        String[] email = account.getEmail().split("@");
        String domain = email.length == 2 ? email[1] : "";
        if (!account.getPassword().isEmpty() && domain.equals("acme.com")) {
            return new ResponseEntity<>(
                    new LinkedHashMap<String, String>(Map.of("name", account.getName(),
                            "lastname", account.getLastname(), "email", account.getEmail())),
                    HttpStatus.ACCEPTED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}


class Account {
    @NonNull
    private String name;
    @NonNull
    private String lastname;
    @NonNull
    private String email;
    @NonNull
    private String password;

    public Account(@NonNull String name, @NonNull String lastname,
                   @NonNull String email, @NonNull String password) {
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
