package com.rafalcendrowski.accountapp.persistance.payment;

import com.rafalcendrowski.accountapp.persistance.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
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
