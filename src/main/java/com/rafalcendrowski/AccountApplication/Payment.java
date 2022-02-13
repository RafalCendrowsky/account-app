package com.rafalcendrowski.AccountApplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.Objects;

@Repository
interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.employee = ?1 AND p.period = ?2")
    Payment findByEmployeePeriod(User employee, String period);

    @Query("SELECT p FROM Payment p WHERE p.employee = ?1")
    Payment[] findByEmployee(User employee);
}

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    User employee;
    private String period;
    private Long salary;


    public Payment() {
    }

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
