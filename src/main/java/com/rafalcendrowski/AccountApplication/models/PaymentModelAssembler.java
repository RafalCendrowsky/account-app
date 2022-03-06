package com.rafalcendrowski.AccountApplication.models;

import com.rafalcendrowski.AccountApplication.controllers.PaymentsController;
import com.rafalcendrowski.AccountApplication.payment.Payment;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<Payment, EntityModel<Payment>> {
    @Override
    public EntityModel<Payment> toModel(Payment payment) {
        return EntityModel.of(payment,
                linkTo(methodOn(PaymentsController.class).getPayment(payment.getId())).withSelfRel(),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments")
        );
    }
}
