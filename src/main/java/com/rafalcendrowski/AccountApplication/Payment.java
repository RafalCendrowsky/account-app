package com.rafalcendrowski.AccountApplication;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
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
@Data
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

    private void setId(Long id) {
        this.id = id;
    }
}
