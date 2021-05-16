package com.learning.employee_management.exception;

import com.learning.employee_management.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SalaryIncompatibleWithRoleException extends Exception {

    public SalaryIncompatibleWithRoleException(Role newRole, double minSalary) {


        super(String.format("Role %s incompatible with employee's salary, raise salary for at least %s first."
                , newRole, minSalary));
    }
}
