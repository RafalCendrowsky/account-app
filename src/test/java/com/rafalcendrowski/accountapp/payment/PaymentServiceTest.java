package com.rafalcendrowski.accountapp.payment;

import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.model.payment.Payment;
import com.rafalcendrowski.accountapp.model.payment.PaymentRepository;
import com.rafalcendrowski.accountapp.model.user.User;
import com.rafalcendrowski.accountapp.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService = new PaymentService();

    @Test
    void addPayment() {
        Payment payment = new Payment();
        paymentService.addPayment(payment);
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
        } catch (EntityNotFoundException ignored) {
        }
        verify(paymentRepository).findByEmployeeAndPeriod(user, "test");
    }

    @Test
    void loadByEmployeeAndPeriod_invalid_args() {
        assertThrows(EntityNotFoundException.class, () -> paymentService.loadByEmployeeAndPeriod(null, "test"));
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