package com.rafalcendrowski.accountapp.mapper;

import com.rafalcendrowski.accountapp.api.employee.request.EmployeeRequest;
import com.rafalcendrowski.accountapp.api.employee.response.EmployeeResponse;
import com.rafalcendrowski.accountapp.model.employee.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface EmployeeMapper {
    EmployeeResponse toResponse(Employee employee);

    Employee toModel(EmployeeRequest request);

    @Mapping(target = "id", ignore = true)
    void updateModelFromRequest(@MappingTarget Employee employee, EmployeeRequest request);
}
