package com.learning.employee_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmployeeAlreadyExistsException extends Exception {


    public EmployeeAlreadyExistsException(String cpf) {

        super(String.format("Employee with cpf %s already exists", cpf));
    }
}
