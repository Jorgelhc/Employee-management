package com.learning.employee_management.entity;

import com.learning.employee_management.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String CPF;

    @Column(nullable = false)
    private double salary;

    @Column(nullable = false)
    private double profitShare;

    @Column(nullable = false)
    private double maxProfitShare;

    @Column(nullable = false)
    private LocalDate admissionDate;


    private LocalDate dismissalDate;

    @Column(nullable = false)
    private Role role;


}



