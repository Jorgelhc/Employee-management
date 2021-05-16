package com.learning.employee_management.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends Exception {

    public EmployeeNotFoundException(Long id) {
        super(String.format("Employee with id %s not found", id));
    }

    public EmployeeNotFoundException(String cpf) {
        super(String.format("Employee with cpf %s not found", cpf));
    }
}
