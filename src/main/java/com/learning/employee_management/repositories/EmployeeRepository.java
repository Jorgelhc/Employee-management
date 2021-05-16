package com.learning.employee_management.repositories;

import com.learning.employee_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByCPF(String cpf);

    Optional<Employee> findById(Long id);


}
