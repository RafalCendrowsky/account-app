package com.rafalcendrowski.AccountApplication.controllers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class Password { // a wrapper object for validation purposes
    @NotEmpty
    @Size(min = 12)
    String password;
}
