package com.rafalcendrowski.accountapp.api.payment.response;

import com.rafalcendrowski.accountapp.api.employee.response.EmployeeResponse;

public record PaymentResponse(String id, EmployeeResponse employee, String period, Long salary) {
}
