package com.learning.employee_management.controller;

import com.learning.employee_management.employeeDTO.EmployeeChangeableValues;
import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.exception.*;
import com.learning.employee_management.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController

@RequestMapping(value = "/api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public List<EmployeeDTO> findAll() {
        return service.findAll();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) throws EmployeeAlreadyExistsException, SalaryIncompatibleWithRoleException, ProfitShareExceedsMaxException {
        return service.createEmployee(employeeDTO);
    }

    @GetMapping(value = "/{cpf}")
    public EmployeeDTO findByCPF(@PathVariable String cpf) throws EmployeeNotFoundException {
        return service.findByCPF(cpf);
    }

    @PatchMapping("/{id}/fireEmployee")
    public EmployeeDTO fireEmployee(@PathVariable Long id) throws EmployeeNotFoundException {
        return service.fireEmployee(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) throws EmployeeNotFoundException {
        service.deleteEmployee(id);
    }

    @PatchMapping("/{id}/raiseSalary")
    public EmployeeDTO raiseSalary(@PathVariable Long id, @RequestBody @Valid EmployeeChangeableValues value) throws EmployeeNotFoundException {
        return service.raiseSalary(id, value.getValue());
    }

    @PatchMapping("/{id}/raiseProfitShare")
    public EmployeeDTO raiseProfitShare(@PathVariable Long id, @RequestBody @Valid EmployeeChangeableValues value) throws ProfitShareExceedsMaxException, EmployeeNotFoundException {
        return service.raiseProfitShare(id, value.getValue());
    }

    @PatchMapping("/{id}/lowerProfitShare")
    public EmployeeDTO lowerProfitShare(@PathVariable Long id, @RequestBody @Valid EmployeeChangeableValues value) throws EmployeeNotFoundException, ProfitShareLesserThanZeroException {
        return service.lowerProfitShare(id, value.getValue());
    }

    @PatchMapping("/{id}/changeRole")
    public EmployeeDTO changeRole(@PathVariable Long id, @RequestBody @Valid EmployeeChangeableValues value) throws SalaryIncompatibleWithRoleException, EmployeeNotFoundException {
        return service.changeRole(id, value.getRole());
    }

}
