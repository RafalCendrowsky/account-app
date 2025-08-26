package com.rafalcendrowski.accountapp.api.payment;

import com.rafalcendrowski.accountapp.api.payment.request.PaymentRequest;
import com.rafalcendrowski.accountapp.api.payment.response.PaymentResponse;
import com.rafalcendrowski.accountapp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ACCOUNTANT')")
@RequiredArgsConstructor
public class PaymentController {
    final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> getPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable String id) {
        return paymentService.getById(id);
    }

    @PostMapping
    public PaymentResponse addPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.create(request);
    }

    @PutMapping("/{id}")
    public PaymentResponse updatePayment(@PathVariable String id, @RequestBody PaymentRequest request) {
        return paymentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable String id) {
        paymentService.delete(id);
    }
}

