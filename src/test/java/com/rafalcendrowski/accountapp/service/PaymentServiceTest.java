package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.payment.request.PaymentRequest;
import com.rafalcendrowski.accountapp.api.payment.response.PaymentResponse;
import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.mapper.PaymentMapper;
import com.rafalcendrowski.accountapp.model.employee.Employee;
import com.rafalcendrowski.accountapp.model.payment.Payment;
import com.rafalcendrowski.accountapp.model.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    EmployeeService employeeService;

    PaymentService paymentService;

    private final Employee employee = new Employee();
    private final Payment payment = new Payment();
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        employee.setId("123");
        employee.setName("John");
        employee.setSurname("Doe");

        payment.setId("1");
        payment.setSalary(100L);
        payment.setPeriod("Test Period");
        payment.setEmployee(employee);

        request = new PaymentRequest(employee.getId(), payment.getPeriod(), payment.getSalary());
        paymentService = new PaymentService(paymentRepository, employeeService, Mappers.getMapper(PaymentMapper.class));
    }

    @Test
    void testFindAll() {
        // Arrange
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        // Act
        List<PaymentResponse> responses = paymentService.findAll();

        // Assert
        assertEquals(1, responses.size());
        var paymentResponse = responses.getFirst();
        assertEquals(payment.getId(), paymentResponse.id());
        assertEquals(payment.getSalary(), paymentResponse.salary());
        assertEquals(payment.getPeriod(), paymentResponse.period());
        assertEquals(employee.getId(), paymentResponse.employee().id());
    }

    @Test
    void testFindByEmployee() {
        // Arrange
        when(employeeService.getEntityById(employee.getId())).thenReturn(employee);
        when(paymentRepository.findByEmployee(employee)).thenReturn(List.of(payment));

        // Act
        List<PaymentResponse> responses = paymentService.findByEmployee(employee.getId());

        // Assert
        assertEquals(1, responses.size());
        assertEquals(payment.getId(), responses.getFirst().id());
    }

    @Test
    void testGetById() {
        // Arrange
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        // Act
        PaymentResponse response = paymentService.getById(payment.getId());

        // Assert
        assertEquals(payment.getId(), response.id());
        assertEquals(payment.getSalary(), response.salary());
        assertEquals(payment.getPeriod(), response.period());
        assertEquals(employee.getId(), response.employee().id());
    }

    @Test
    void testGetById_NotFound() {
        // Arrange
        String paymentId = "2";
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> paymentService.getById(paymentId));
    }

    @Test
    void testCreate() {
        // Arrange
        when(paymentRepository.save(any(Payment.class))).then(it -> {
            ((Payment) it.getArgument(0)).setId(payment.getId());
            return null;
        });

        // Act
        PaymentResponse response = paymentService.create(request);

        // Assert
        assertEquals(payment.getId(), response.id());
        assertEquals(payment.getSalary(), response.salary());
        assertEquals(payment.getPeriod(), response.period());
        assertEquals(employee.getId(), response.employee().id());
        verify(paymentRepository).save(any());
    }

    @Test
    void testCreate_noEmployee() {

        // Arrange
        request = new PaymentRequest("nonexistent", payment.getPeriod(), payment.getSalary());
        when(employeeService.getEntityById(request.employeeId())).thenThrow(new EntityNotFoundException(
                "Employee not found"));

        // Act && Assert
        assertThrows(EntityNotFoundException.class, () -> paymentService.create(request));
    }

    @Test
    void testUpdate() {
        // Arrange
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        // Act
        PaymentResponse response = paymentService.update(payment.getId(), request);

        // Assert
        assertEquals(payment.getId(), response.id());
        verify(paymentRepository).save(payment);
    }

    @Test
    void testUpdate_noEmployee() {
        // Arrange
        request = new PaymentRequest("nonexistent", payment.getPeriod(), payment.getSalary());
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(employeeService.getEntityById(request.employeeId())).thenThrow(new EntityNotFoundException(
                "Employee not found"));

        // Act && Assert
        assertThrows(EntityNotFoundException.class, () -> paymentService.update(payment.getId(), request));
    }

    @Test
    void testDelete() {
        // Arrange
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        // Act
        paymentService.delete(payment.getId());

        // Assert
        verify(paymentRepository).delete(payment);
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> paymentService.delete(payment.getId()));
        verify(paymentRepository, never()).delete(any());
    }
}