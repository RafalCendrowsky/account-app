package com.rafalcendrowski.accountapp.persistance.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String>, RevisionRepository<Employee, String, Long> {
}
