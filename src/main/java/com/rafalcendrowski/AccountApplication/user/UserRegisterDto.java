package com.rafalcendrowski.AccountApplication.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastname;
    @NotNull
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String email;
    @NotEmpty
    @Size(min = 12)
    @JsonIgnore
    private String password;
}