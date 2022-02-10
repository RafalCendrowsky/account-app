package com.rafalcendrowski.AccountApplication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> addAccount(@RequestBody Account account) {
        if (account.getPassword().isEmpty() ||
                !account.getEmail().split("@")[1].equals("acme.com")
           ) {
            return new ResponseEntity<Map<String, String>>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<Map<String, String>>(
                    Map.of("name", account.getName(), "lastname", account.getLastname(),
                    "email", account.getEmail()), HttpStatus.ACCEPTED);
        }
    }

}

class Account {
    private String name;
    private String lastname;
    private String email;
    private String password;

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
