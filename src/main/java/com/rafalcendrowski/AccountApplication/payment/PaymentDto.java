package com.rafalcendrowski.AccountApplication.controllers;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class PaymentDto {
    @NotEmpty
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String employee;
    @NotEmpty
    @Pattern(regexp = "(1[0-2]|0[1-9])-\\d\\d\\d\\d")
    private String period;
    @Min(0L)
    private Long salary;
}
