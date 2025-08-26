package com.rafalcendrowski.accountapp.model.employee;

import com.rafalcendrowski.accountapp.model.payment.Payment;
import com.rafalcendrowski.accountapp.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
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
