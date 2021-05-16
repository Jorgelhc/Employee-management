package com.learning.employee_management.mapper;


import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);


    default Employee toModel(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            return null;
        }

        Employee employee = new Employee();

        employee.setId(employeeDTO.getId());
        employee.setName(employeeDTO.getName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setCPF(employeeDTO.getCPF());
        employee.setSalary(employeeDTO.getSalary());
        employee.setProfitShare(employeeDTO.getProfitShare());
        employee.setMaxProfitShare(employeeDTO.getMaxProfitShare());
        employee.setAdmissionDate(employeeDTO.getAdmissionDate());

        if (employeeDTO.getDismissalDate() != null) {
            employee.setDismissalDate(employeeDTO.getDismissalDate());
        }

        employee.setRole(employeeDTO.getRole());

        return employee;

    }

    default EmployeeDTO toDTO(Employee employee) {

        if (employee == null) {
            return null;
        }

        EmployeeDTO employeeDTO = new EmployeeDTO();

        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setCPF(employee.getCPF());
        employeeDTO.setSalary(employee.getSalary());
        employeeDTO.setProfitShare(employee.getProfitShare());
        employeeDTO.setMaxProfitShare(employee.getMaxProfitShare());
        employeeDTO.setAdmissionDate(employee.getAdmissionDate());

        if (employee.getDismissalDate() != null) {
            employeeDTO.setDismissalDate(employee.getDismissalDate());
        }

        employeeDTO.setRole(employee.getRole());

        return employeeDTO;


    }


}
