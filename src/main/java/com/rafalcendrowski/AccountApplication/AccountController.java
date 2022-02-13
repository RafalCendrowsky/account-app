package com.rafalcendrowski.AccountApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.*;

@RestController
@RequestMapping("/api/acct")
public class AccountController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    @PostMapping("/payment")
    public void postPayrolls(@Valid @RequestBody PaymentBody[] payments) {
        for(PaymentBody paymentBody : payments) {
            User employee = userRepository.findByUsername(paymentBody.getEmployee());
            if (employee == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found");
            } else if (paymentRepository.findByEmployeePeriod(employee, paymentBody.getPeriod()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary for selected period/employee pair already exists");
            } else {
                Payment payment = new Payment();
                payment.setEmployee(employee);
                payment.setPeriod(paymentBody.getPeriod());
                payment.setSalary(paymentBody.getSalary());
                employee.addPayment(payment);
                paymentRepository.save(payment);
                userRepository.save(employee);

            }
        }
    }
}

class PaymentBody {
    @NotEmpty
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String employee;
    @NotEmpty
    @Pattern(regexp = "\\d\\d-\\d\\d\\d\\d")
    private String period;
    @Min(0L)
    private Long salary;

    public PaymentBody(String email, String period, Long salary) {
        this.employee = email;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}

