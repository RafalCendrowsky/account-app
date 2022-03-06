package com.rafalcendrowski.AccountApplication.payment;

import com.rafalcendrowski.AccountApplication.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        verify(paymentRepository).findByEmployeeAndPeriod(user, "test");
    }

    @Test
    void loadByEmployeeAndPeriod_invalid_args() {
        assertThrows(IllegalArgumentException.class, () -> paymentService.loadByEmployeeAndPeriod(null, "test"));
    }

    @Test
    void loadByEmployee() {
        Payment payment = new Payment();
        User user = new User();
        when(paymentRepository.findByEmployee(user)).thenReturn(List.of(payment));
        assertEquals(paymentService.loadByEmployee(user), List.of(payment));
    }

    @Test
    void hasPayment() {
        User user = new User();
        when(paymentRepository.findByEmployeeAndPeriod(user, "test")).thenReturn(new Payment());
        assertTrue(paymentService.hasPayment(user, "test"));
        assertFalse(paymentService.hasPayment(user, "not test"));
    }
}