package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.employee.request.EmployeeRequest;
import com.rafalcendrowski.accountapp.api.employee.response.EmployeeResponse;
import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.mapper.EmployeeMapper;
import com.rafalcendrowski.accountapp.persistance.employee.Employee;
import com.rafalcendrowski.accountapp.persistance.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse getById(String id) {
        var employee = getEntityById(id);
        return employeeMapper.toResponse(employee);
    }

    public Employee getEntityById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    public EmployeeResponse create(EmployeeRequest request) {
        var employee = employeeMapper.toModel(request);
        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }

    public EmployeeResponse update(String id, EmployeeRequest request) {
        var employee = getEntityById(id);
        employeeMapper.updateModelFromRequest(employee, request);
        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }

    public void delete(String id) {
        Employee employee = getEntityById(id);
        employeeRepository.delete(employee);
    }

    public void verifyHasEmployee(String id) {
        getEntityById(id);
    }
}