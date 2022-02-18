package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.employee = ?1 AND p.period = ?2")
    Payment findByEmployeePeriod(User employee, String period);

    @Query("SELECT p FROM Payment p WHERE p.employee = ?1")
    Payment[] findByEmployee(User employee);
}
