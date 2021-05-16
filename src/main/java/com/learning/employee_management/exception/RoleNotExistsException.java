package com.learning.employee_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotExistsException extends Exception {

    public RoleNotExistsException(String newRole) {

        super(String.format("Role %s not exists", newRole));
    }

}
