package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;

import java.util.List;

public interface PaymentService {

    Payment savePayment(Payment payment);

    void deletePayment(Payment payment);

    Payment loadByEmployeeAndPeriod(User user, String period) throws IllegalArgumentException;

    List<Payment> loadByEmployee(User employee);

    boolean hasPayment(User employee, String period);
}