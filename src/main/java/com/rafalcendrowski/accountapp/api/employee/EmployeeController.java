package com.rafalcendrowski.accountapp.api.employee;

import com.rafalcendrowski.accountapp.api.common.AuditResponse;
import com.rafalcendrowski.accountapp.api.employee.request.EmployeeRequest;
import com.rafalcendrowski.accountapp.api.employee.response.EmployeeResponse;
import com.rafalcendrowski.accountapp.api.payment.response.PaymentResponse;
import com.rafalcendrowski.accountapp.service.EmployeeService;
import com.rafalcendrowski.accountapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ACCOUNTANT')")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final PaymentService paymentService;

    @GetMapping
    public List<EmployeeResponse> findAllEmployees() {
        return employeeService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployee(@PathVariable String id) {
        return employeeService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public EmployeeResponse updateEmployee(@PathVariable String id, @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteEmployee(@PathVariable String id) {
        employeeService.delete(id);
    }

    @GetMapping("/{id}/payment}")
    public List<PaymentResponse> getPaymentsForEmployee(@PathVariable String id) {
        return paymentService.findByEmployee(id);
    }

    @GetMapping("/{id}/audit")
    public List<AuditResponse<EmployeeResponse>> getEmployeeAuditLogs(@PathVariable String id) {
        return employeeService.findAuditLogsById(id);
    }
}
