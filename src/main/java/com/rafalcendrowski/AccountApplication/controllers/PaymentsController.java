package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    final UserService userService;
    final PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> getPayments() {
        return paymentService.loadAllPayments().stream()
                .map(PaymentDto::of)
                .toList();
    }

    @GetMapping("/{id}")
    public PaymentDto getPayment(@PathVariable Long id) {
        var payment = paymentService.loadById(id);
        return PaymentDto.of(payment);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentDto> getPaymentsByUserId(@PathVariable Long userId) {
        var employee = userService.loadById(userId);
        return paymentService.loadByEmployee(employee).stream()
                .map(PaymentDto::of)
                .toList();
    }

    @GetMapping("/find")
    public PaymentDto getPaymentByUserAndPeriod(@Valid @RequestBody PaymentDto paymentDto) {
        var employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        return PaymentDto.of(payment);
    }

    @GetMapping("/find/{username}")
    public List<PaymentDto> getPaymentsByUsername(@PathVariable String username) {
        var employee = userService.loadByUsername(username);
        return paymentService.loadByEmployee(employee).stream()
                .map(PaymentDto::of)
                .toList();
    }

    @Transactional
    @PostMapping
    public List<PaymentDto> addPayrolls(@Valid @RequestBody PaymentList<PaymentDto> payments) {
        List<Payment> paymentList = new ArrayList<>();
        for (PaymentDto paymentDto : payments) {
            var employee = userService.loadByUsername(paymentDto.getEmployee());
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
       return paymentList.stream()
                .map(PaymentDto::of)
               .toList();
    }

    @PutMapping
    public PaymentDto updatePayroll(@Valid @RequestBody PaymentDto paymentDto) {
        User employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        payment.setSalary(paymentDto.getSalary());
        paymentService.savePayment(payment);
        return PaymentDto.of(payment);
    }

    @PutMapping("/{id}")
    public PaymentDto updatePayroll(@PathVariable Long id, @RequestBody Long salary) {
        if (salary < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be a non-negative number");
        }
        Payment payment = paymentService.loadById(id);
        payment.setSalary(salary);
        paymentService.savePayment(payment);
        return PaymentDto.of(payment);
    }

    @DeleteMapping
    public void deletePayroll(@Valid @RequestBody PaymentDto paymentBody) {
        User employee = userService.loadByUsername(paymentBody.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentBody.getPeriod());
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
    }

    @DeleteMapping("/{id}")
    public void deletePayroll(@PathVariable Long id) {
        Payment payment = paymentService.loadById(id);
        User employee = payment.getEmployee();
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
    }
}

