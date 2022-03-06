package com.rafalcendrowski.AccountApplication.models;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class PaymentModelAssembler implements RepresentationModelAssembler<Payment, EntityModel<PaymentDto>> {
    @Override
    public EntityModel<PaymentDto> toModel(Payment entity) {
        return null;
    }
}
