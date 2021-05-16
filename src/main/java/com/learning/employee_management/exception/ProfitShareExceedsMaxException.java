package com.learning.employee_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfitShareExceedsMaxException extends Exception {

    public ProfitShareExceedsMaxException(double maxValue) {

        super(String.format("Profit share exceeds maximum value allowed (Max = %s)", maxValue));
    }
}
