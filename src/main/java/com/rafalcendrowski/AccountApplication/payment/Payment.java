package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    User employee;
    private String period;
    private Long salary;

    public Payment(User employee, String period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return employee.equals(payment.getEmployee()) && period.equals(payment.getPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }

    private void setId(Long id) {}
}
