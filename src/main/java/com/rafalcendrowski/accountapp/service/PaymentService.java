package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.payment.request.PaymentRequest;
import com.rafalcendrowski.accountapp.api.payment.response.PaymentResponse;
import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.mapper.PaymentMapper;
import com.rafalcendrowski.accountapp.model.payment.Payment;
import com.rafalcendrowski.accountapp.model.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EmployeeService employeeService;
    private final PaymentMapper paymentMapper;

    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    public List<PaymentResponse> findByEmployee(String employeeId) {
        var employee = employeeService.getEntityById(employeeId);
        return paymentRepository.findByEmployee(employee).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse getById(String id) {
        var payment = getEntityById(id);
        return paymentMapper.toResponse(payment);
    }

    private Payment getEntityById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

    }

    public PaymentResponse create(PaymentRequest request) {
        var payment = paymentMapper.toModel(request);
        savePayment(payment);
        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse update(String id, PaymentRequest request) {
        var payment = getEntityById(id);
        paymentMapper.updateModelFromRequest(payment, request);
        savePayment(payment);
        return paymentMapper.toResponse(payment);
    }

    private void savePayment(Payment payment) {
        try {
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new EntityNotFoundException("Employee not found");
        }
    }


    public void delete(String id) {
        var payment = getEntityById(id);
        paymentRepository.delete(payment);
    }
}