package com.learning.employee_management.services;

import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.entity.Employee;
import com.learning.employee_management.enums.Role;
import com.learning.employee_management.exception.*;
import com.learning.employee_management.mapper.EmployeeMapper;
import com.learning.employee_management.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final double OWNER_MIN_SALARY = 35000;
    private final double DIRECTOR_MIN_SALARY = 30000;
    private final double MANAGER_MIN_SALARY = 10000;
    private final double SPECIALIST_MIN_SALARY = 8000;
    private final double TECHNICIAN_MIN_SALARY = 5000;
    @Autowired
    private EmployeeRepository repository;

    public List<EmployeeDTO> findAll() {

        List<Employee> allEmployees = repository.findAll();

        return allEmployees.stream().map(EmployeeMapper.INSTANCE::toDTO).collect(Collectors.toList());

    }


    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) throws EmployeeAlreadyExistsException, SalaryIncompatibleWithRoleException, ProfitShareExceedsMaxException {


        verifyIfAlreadyExists(employeeDTO.getCPF());
        Employee employee = EmployeeMapper.INSTANCE.toModel(employeeDTO);
        verifyRole(employee, employee.getRole());
        verifyRaiseProfitShare(employee.getProfitShare(), employee.getMaxProfitShare());
        Employee savedEmployee = repository.save(employee);

        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);
    }

    public EmployeeDTO findByCPF(String cpf) throws EmployeeNotFoundException {
        Employee foundEmployee = repository.findByCPF(cpf).orElseThrow(() -> new EmployeeNotFoundException(cpf));
        return EmployeeMapper.INSTANCE.toDTO(foundEmployee);

    }

    public EmployeeDTO fireEmployee(Long id) throws EmployeeNotFoundException {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        employee.setDismissalDate(LocalDate.now());
        Employee savedEmployee = repository.save(employee);

        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);
    }

    public void deleteEmployee(Long id) throws EmployeeNotFoundException {

        verifyIfExists(id);
        repository.deleteById(id);

    }

    public EmployeeDTO raiseSalary(Long id, double value) throws EmployeeNotFoundException {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        double newSalary = employee.getSalary() + value;

        employee.setSalary(newSalary);
        Employee savedEmployee = repository.save(employee);

        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);
    }

    public EmployeeDTO raiseProfitShare(Long id, double value) throws ProfitShareExceedsMaxException, EmployeeNotFoundException {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        double newProfitShare = employee.getProfitShare() + value;
        verifyRaiseProfitShare(newProfitShare, employee.getMaxProfitShare());
        employee.setProfitShare(newProfitShare);
        Employee savedEmployee = repository.save(employee);
        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);


    }


    public EmployeeDTO lowerProfitShare(Long id, double value) throws EmployeeNotFoundException, ProfitShareLesserThanZeroException {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        double newProfitShare = employee.getProfitShare() - value;
        verifyLowerProfitShare(newProfitShare);
        employee.setProfitShare(newProfitShare);
        Employee savedEmployee = repository.save(employee);
        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);


    }


    public EmployeeDTO changeRole(Long id, Role newRole) throws SalaryIncompatibleWithRoleException, EmployeeNotFoundException {

        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        verifyRole(employee, newRole);
        employee.setRole(newRole);
        Employee savedEmployee = repository.save(employee);

        return EmployeeMapper.INSTANCE.toDTO(savedEmployee);
    }


    public void verifyIfExists(Long id) throws EmployeeNotFoundException {

        repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

    }


    public void verifyIfAlreadyExists(String cpf) throws EmployeeAlreadyExistsException {

        if (repository.findByCPF(cpf).isPresent()) {

            throw new EmployeeAlreadyExistsException(cpf);
        }
    }

    public void verifyRole(Employee employee, Role Role) throws SalaryIncompatibleWithRoleException {

        double salary = employee.getSalary();
        switch (Role) {

            case OWNER: {
                if (salary < OWNER_MIN_SALARY) {
                    throw new SalaryIncompatibleWithRoleException(Role, OWNER_MIN_SALARY);
                }
                break;
            }

            case DIRECTOR: {
                if (salary < DIRECTOR_MIN_SALARY) {
                    throw new SalaryIncompatibleWithRoleException(Role, DIRECTOR_MIN_SALARY);
                }
                break;
            }

            case MANAGER: {
                if (salary < MANAGER_MIN_SALARY) {
                    throw new SalaryIncompatibleWithRoleException(Role, MANAGER_MIN_SALARY);
                }
                break;
            }
            case SPECIALIST: {
                if (salary < SPECIALIST_MIN_SALARY) {
                    throw new SalaryIncompatibleWithRoleException(Role, SPECIALIST_MIN_SALARY);
                }
                break;
            }
            case TECHNICIAN: {
                if (salary < TECHNICIAN_MIN_SALARY) {
                    throw new SalaryIncompatibleWithRoleException(Role, TECHNICIAN_MIN_SALARY);
                }
                break;
            }

        }
    }

    public void verifyRaiseProfitShare(double profitShare, double maxProfitShare) throws ProfitShareExceedsMaxException {

        if (profitShare >= maxProfitShare) {

            throw new ProfitShareExceedsMaxException(maxProfitShare);

        }
    }

    public void verifyLowerProfitShare(double profitShare) throws ProfitShareLesserThanZeroException {

        if (profitShare < 0) {

            throw new ProfitShareLesserThanZeroException();

        }
    }

}


