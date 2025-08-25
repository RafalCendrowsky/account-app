package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.models.PaymentModelAssembler;
import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    final UserService userService;
    final PaymentService paymentService;
    final PaymentModelAssembler paymentModelAssembler;

    @GetMapping
    public CollectionModel<EntityModel<PaymentDto>> getPayments() {
        List<EntityModel<PaymentDto>> payments = paymentService.loadAllPayments().stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPayments()).withSelfRel(),
                linkTo(methodOn(PaymentsController.class).getPaymentByUserAndPeriod(null)).withRel("search"),
                linkTo(methodOn(PaymentsController.class).getPaymentsByUsername(null)).withRel("search"),
                linkTo(methodOn(PaymentsController.class).getPaymentsByUserId(null)).withRel("search"));
    }

    @GetMapping("/{id}")
    public EntityModel<PaymentDto> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.loadById(id);
        return paymentModelAssembler.toModel(payment);
    }

    @GetMapping("/user/{userId}")
    public CollectionModel<EntityModel<PaymentDto>> getPaymentsByUserId(@PathVariable Long userId) {
        User employee = userService.loadById(userId);
        List<EntityModel<PaymentDto>> payments = paymentService.loadByEmployee(employee).stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPaymentsByUserId(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUser(userId)).withRel("user"),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @GetMapping("/find")
    public EntityModel<PaymentDto> getPaymentByUserAndPeriod(@Valid @RequestBody PaymentDto paymentDto) {
        User employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        return paymentModelAssembler.toModel(payment);
    }

    @GetMapping("/find/{username}")
    public CollectionModel<EntityModel<PaymentDto>> getPaymentsByUsername(@PathVariable String username) {
        User employee = userService.loadByUsername(username);
        List<EntityModel<PaymentDto>> payments = paymentService.loadByEmployee(employee).stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPaymentsByUsername(username)).withSelfRel(),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @Transactional
    @PostMapping
    public CollectionModel<EntityModel<PaymentDto>> addPayrolls(@Valid @RequestBody PaymentList<PaymentDto> payments) {
        List<Payment> paymentList = new ArrayList<>();
        for (PaymentDto paymentDto : payments) {
            User employee = userService.loadByUsername(paymentDto.getEmployee());
            if (paymentService.hasPayment(employee, paymentDto.getPeriod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Payment for %s in %s already exists".formatted(paymentDto.getEmployee(), paymentDto.getPeriod()));
            } else {
                Payment payment = new Payment(employee, paymentDto.getPeriod(), paymentDto.getSalary());
                paymentList.add(payment);
                employee.addPayment(payment);
                paymentService.savePayment(payment);
                userService.updateUser(employee);
            }
        }
        List<EntityModel<PaymentDto>> entityList = paymentList.stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(entityList,
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @PutMapping
    public EntityModel<PaymentDto> updatePayroll(@Valid @RequestBody PaymentDto paymentDto) {
        User employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        payment.setSalary(paymentDto.getSalary());
        paymentService.savePayment(payment);
        return paymentModelAssembler.toModel(payment);
    }

    @PutMapping("/{id}")
    public EntityModel<PaymentDto> updatePayroll(@PathVariable Long id, @RequestBody Long salary) {
        if (salary < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be a non-negative number");
        }
        Payment payment = paymentService.loadById(id);
        payment.setSalary(salary);
        paymentService.savePayment(payment);
        return paymentModelAssembler.toModel(payment);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePayroll(@Valid @RequestBody PaymentDto paymentBody) {
        User employee = userService.loadByUsername(paymentBody.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentBody.getPeriod());
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayroll(@PathVariable Long id) {
        Payment payment = paymentService.loadById(id);
        User employee = payment.getEmployee();
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
        return ResponseEntity.noContent().build();
    }
}

