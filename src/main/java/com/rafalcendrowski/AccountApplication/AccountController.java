package com.rafalcendrowski.AccountApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Objects;

@RestController
@RequestMapping("/api/acct")
public class AccountController {

    @PostMapping("/payment")
    public void postPayrolls(@Valid @RequestBody Payment[] payments) {

    }
}

interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByEmployee(User employee);
}

@Entity
@Table(name="payments")
class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    User employee;
    private String period;
    private Long salary;


    public Payment() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return employee.equals(payment.employee) && period.equals(payment.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
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

    public Long getId() {
        return id;
    }
}