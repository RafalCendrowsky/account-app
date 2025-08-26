package com.rafalcendrowski.accountapp.api.payment.request;

public record PaymentRequest(String id, String employeeId, String period, Long salary) {
}
