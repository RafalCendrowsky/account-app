package com.rafalcendrowski.AccountApplication.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService = new PaymentServiceImpl();

    @Test
    void savePayment() {
    }

    @Test
    void deletePayment() {
    }

    @Test
    void loadByEmployeeAndPeriod() {
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