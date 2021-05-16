package com.learning.employee_management.employeeDTO;


import com.learning.employee_management.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmployeeChangeableValues {


    private double value;

    private Role role;


}