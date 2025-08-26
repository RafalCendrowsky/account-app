package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/empl")
@RequiredArgsConstructor
public class EmployeeController {
    final PaymentService paymentService;;

    @GetMapping
    public UserDto getEmployee(@AuthenticationPrincipal User user) {
        return UserDto.of(user);
    }

    @GetMapping(value = "/payment", params = {"period"})
    public PaymentDto getPayment(@RequestParam String period,
                                              @AuthenticationPrincipal User user) {
        // check that the period is a valid date in the mm-yyyy format
        if (!period.matches("(0[1-9]|1[0-2])-\\d\\d\\d\\d")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
        }
        var payment = paymentService.loadByEmployeeAndPeriod(user, period);
        return PaymentDto.of(payment);
    }

    @GetMapping("/payment")
    public List<PaymentDto> getPayments(@AuthenticationPrincipal User user) {
        return paymentService.loadByEmployee(user).stream()
                .map(PaymentDto::of)
                .toList();
    }
}
