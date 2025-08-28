package com.rafalcendrowski.accountapp.mapper;

import com.rafalcendrowski.accountapp.api.payment.request.PaymentRequest;
import com.rafalcendrowski.accountapp.api.payment.response.PaymentResponse;
import com.rafalcendrowski.accountapp.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "employee.id", source = "employeeId")
    Payment toModel(PaymentRequest paymentRequest);

    @Mapping(target = "employee.id", source = "employeeId")
    void updateModelFromRequest(@MappingTarget Payment payment, PaymentRequest request);
}
