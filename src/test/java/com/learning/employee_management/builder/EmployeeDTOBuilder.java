package com.learning.employee_management.builder;

import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.enums.Role;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public class EmployeeDTOBuilder {


    @Builder.Default
    private final Long id = 4L;

    @Builder.Default
    private final String name = "joao";

    @Builder.Default
    private final String lastName = "pedro";

    @Builder.Default
    private final String CPF = "35642145685";

    @Builder.Default
    private final double salary = 30000;

    @Builder.Default
    private final double profitShare = 200;

    @Builder.Default
    private final double maxProfitShare = 1000;

    @Builder.Default
    private final LocalDate admissionDate = LocalDate.of(2020, 2, 5);


    @Builder.Default
    private final Role role = Role.MANAGER;


    public EmployeeDTO toEmployeeDTO() {
        return new EmployeeDTO(id,
                name, lastName, CPF, salary, profitShare,
                maxProfitShare, admissionDate, role);
    }
}