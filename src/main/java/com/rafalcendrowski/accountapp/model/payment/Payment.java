package com.rafalcendrowski.accountapp.model.payment;

import com.rafalcendrowski.accountapp.model.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String period;
    private Long salary;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Employee employee;
}
