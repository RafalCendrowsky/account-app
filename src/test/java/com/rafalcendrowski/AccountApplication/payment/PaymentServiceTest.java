package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService = new PaymentServiceImpl();

    @Test
    void savePayment() {
        Payment payment = new Payment();
        paymentService.savePayment(payment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void deletePayment() {
        Payment payment = new Payment();
        paymentService.deletePayment(payment);
        verify(paymentRepository).delete(payment);
    }

    @Test
    void loadByEmployeeAndPeriod() {
        User user = new User();
        try {
            paymentService.loadByEmployeeAndPeriod(user, "test");
        } catch (IllegalArgumentException ignored) {}
        verify(paymentRepository).findByEmployeePeriod(user, "test");
    }

    @Test
    void loadByEmployeeAndPeriod_invalid_args() {
    }

    @Test
    void loadByEmployee() {
    }

    @Test
    void loadByEmployee_invalid_arg() {
    }

    @Test
    void hasPayment() {
    }
}