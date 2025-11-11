package com.rafalcendrowski.accountapp.persistance.payment;

import com.rafalcendrowski.accountapp.persistance.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByEmployeeAndPeriod(Employee employee, String period);

    List<Payment> findByEmployee(Employee employee);
}
