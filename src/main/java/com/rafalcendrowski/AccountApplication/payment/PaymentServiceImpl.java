package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }

    public Payment loadByEmployeeAndPeriod(User user, String period) throws IllegalArgumentException{
        Payment payment = paymentRepository.findByEmployeePeriod(user, period);
        if (payment == null) {
            throw new IllegalArgumentException("Payment does not exist");
        } else {
            return payment;
        }
    }

    public List<Payment> loadByEmployee(User employee) {
        return Arrays.stream(paymentRepository.findByEmployee(employee)).toList();
    }

    public boolean hasPayment(User employee, String period) {
        Payment payment = paymentRepository.findByEmployeePeriod(employee, period);
        return (payment != null);
    }
}
