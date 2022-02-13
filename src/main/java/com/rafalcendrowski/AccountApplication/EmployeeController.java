package com.rafalcendrowski.AccountApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Pattern;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @GetMapping(value = "/payment", params = {"period"})
    public Map<String, Object> getPayment(@Validated @RequestParam @Pattern(regexp = "\\d\\d-\\d\\d\\d\\d") String period,
                                          @AuthenticationPrincipal User user) {
        Payment payment = paymentRepository.findByEmployeePeriod(user, period);
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment for %s not found".formatted(period));
        } else {
            return getPaymentMap(user, payment);
        }
    }

    private Map<String, Object> getPaymentMap(User user, Payment payment) {
        String month = DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(payment.getPeriod().substring(0,2))-1];
        return Map.of("name", user.getName(), "lastname", user.getLastName(),
                "period", month + payment.getPeriod().substring(2), "salary", payment.getSalary());
    }

    @GetMapping("/payment")
    public List<Map<String, Object>> getPayment(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> payments = new ArrayList<>();
        for(Payment payment: paymentRepository.findByEmployee(user)) {
            payments.add(getPaymentMap(user, payment));
        }
        return payments;
    }
}
