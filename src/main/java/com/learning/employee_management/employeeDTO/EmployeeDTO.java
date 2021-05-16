package com.learning.employee_management.employeeDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.employee_management.enums.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data

@NoArgsConstructor
public class EmployeeDTO {

    private Long id;
    @NotNull
    @Size(min = 3, max = 10)
    private String name;
    @NotNull
    @Size(min = 3, max = 60)
    private String lastName;
    @NotNull
    @Pattern(regexp = "[0-9]{11}")
    private String CPF;
    @NotNull
    @Min(1100)
    private double salary;
    @NotNull
    private double profitShare;
    @NotNull
    private double maxProfitShare;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate admissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dismissalDate;
    @NotNull
    private Role role;

    @Builder
    public EmployeeDTO(Long id, String name, String lastName, String CPF,
                       double salary, double profitShare, double maxProfitShare,
                       LocalDate admissionDate, Role role) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.CPF = CPF;
        this.salary = salary;
        this.profitShare = profitShare;
        this.maxProfitShare = maxProfitShare;
        this.admissionDate = admissionDate;
        this.role = role;
    }


}