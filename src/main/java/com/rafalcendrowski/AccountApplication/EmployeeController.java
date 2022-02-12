package com.rafalcendrowski.AccountApplication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {


    @GetMapping("/payment")
    public Map<String, Object> getPayment(@AuthenticationPrincipal User user) {
        return user.getUserMap();
    }
}
