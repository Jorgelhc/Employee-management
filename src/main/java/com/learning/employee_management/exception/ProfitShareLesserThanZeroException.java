package com.learning.employee_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfitShareLesserThanZeroException extends Exception {

    public ProfitShareLesserThanZeroException() {

        super("Profit share final value is lesser than zero");
    }
}
