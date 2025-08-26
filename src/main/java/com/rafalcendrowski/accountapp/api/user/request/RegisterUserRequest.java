package com.rafalcendrowski.accountapp.api.user.request;

import jakarta.validation.constraints.*;

public record RegisterUserRequest(
        @NotNull
        @Email
        @Pattern(regexp = ".*@acme\\.com")
        String email,

        @NotEmpty
        @Size(min = 12)
        String password,

        String employeeId
) {
}