package com.learning.employee_management.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    OWNER("Owner"),
    DIRECTOR("Director"),
    MANAGER("Manager"),
    SPECIALIST("Specialist"),
    TECHNICIAN("Technician");


    private final String description;
}
