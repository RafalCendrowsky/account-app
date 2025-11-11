package com.rafalcendrowski.accountapp.persistance.employee;

import com.rafalcendrowski.accountapp.persistance.payment.Payment;
import com.rafalcendrowski.accountapp.persistance.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Audited
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String surname;

    @Column(unique = true)
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "employee")
    private List<Payment> payments;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
