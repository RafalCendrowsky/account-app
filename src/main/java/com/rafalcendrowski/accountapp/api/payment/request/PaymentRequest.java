package com.rafalcendrowski.accountapp.api.payment.request;

public record PaymentRequest(String employeeId, String period, Long salary) {
}
