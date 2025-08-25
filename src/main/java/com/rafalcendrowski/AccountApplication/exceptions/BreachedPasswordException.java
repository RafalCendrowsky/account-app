package com.rafalcendrowski.AccountApplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password has been breached")
public class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException() {
        super();
    }
}
