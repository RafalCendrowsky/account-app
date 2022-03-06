package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.exceptions.CustomNotFoundException;
import com.rafalcendrowski.AccountApplication.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public Payment loadById(Long id) throws CustomNotFoundException {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if(optionalPayment.isEmpty()) {
            throw new CustomNotFoundException("Payment not found");
        } else {
            return optionalPayment.get();
        }
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