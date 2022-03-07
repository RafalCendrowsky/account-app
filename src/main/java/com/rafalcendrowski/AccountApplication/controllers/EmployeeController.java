package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.models.EmployeeModelAssembler;
import com.rafalcendrowski.AccountApplication.models.EmployeePaymentModelAssembler;
import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    EmployeePaymentModelAssembler employeePaymentModelAssembler;

    @Autowired
    EmployeeModelAssembler employeeModelAssembler;
    @GetMapping
    public EntityModel<UserDto> getEmployee(@AuthenticationPrincipal User user) {
        return employeeModelAssembler.toModel(user);
    }

    @GetMapping(value = "/payment", params = {"period"})
    public EntityModel<PaymentDto> getPayment(@RequestParam String period,
                                              @AuthenticationPrincipal User user) {
        if (!period.matches("(0[1-9]|1[0-2])-\\d\\d\\d\\d")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
        }
        Payment payment = paymentService.loadByEmployeeAndPeriod(user, period);
        return employeePaymentModelAssembler.toModel(payment);
    }

    @GetMapping("/payment")
    public CollectionModel<EntityModel<PaymentDto>> getPayments(@AuthenticationPrincipal User user) {
        List<EntityModel<PaymentDto>> payments = new ArrayList<>();
        paymentService.loadByEmployee(user).stream()
                .map(employeePaymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(EmployeeController.class).getPayments(null)).withSelfRel());
    }
}
