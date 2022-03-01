package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;

import java.util.List;

public interface PaymentService {

    public void savePayment(Payment payment);

    public void deletePayment(Payment payment);

    public Payment loadByEmployeeAndPeriod(User user, String period);

    public List<Payment> loadByEmployee(User employee);

    public boolean hasPayment(User employee, String period);
}