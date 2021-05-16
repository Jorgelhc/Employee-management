package com.learning.employee_management.builder;

import com.learning.employee_management.employeeDTO.EmployeeChangeableValues;
import com.learning.employee_management.enums.Role;
import lombok.Builder;


@Builder
public class EmployeeChangeableValuesBuilder {


    @Builder.Default
    private final double value = 100;

    @Builder.Default
    private final Role role = Role.TECHNICIAN;


    public EmployeeChangeableValues toEmployeeChangeableValues() {
        return new EmployeeChangeableValues(value, role);
    }
}