package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.exceptions.CustomNotFoundException;
import com.rafalcendrowski.AccountApplication.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }

    @Override
    public Payment loadByEmployeeAndPeriod(User user, String period) throws CustomNotFoundException {
        Payment payment = paymentRepository.findByEmployeeAndPeriod(user, period);
        if (payment == null) {
            throw new CustomNotFoundException("Payment does not exist");
        } else {
            return payment;
        }
    }

    @Override
    public List<Payment> loadByEmployee(User employee) {
        return paymentRepository.findByEmployee(employee);
    }

    @Override
    public List<Payment> loadAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public boolean hasPayment(User employee, String period) {
        Payment payment = paymentRepository.findByEmployeeAndPeriod(employee, period);
        return (payment != null);
    }
}