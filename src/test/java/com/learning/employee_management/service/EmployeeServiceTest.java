package com.learning.employee_management.service;

import com.learning.employee_management.builder.EmployeeDTOBuilder;
import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.entity.Employee;
import com.learning.employee_management.enums.Role;
import com.learning.employee_management.exception.*;
import com.learning.employee_management.mapper.EmployeeMapper;
import com.learning.employee_management.repositories.EmployeeRepository;
import com.learning.employee_management.services.EmployeeService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {


    private final EmployeeMapper employeeMapper = EmployeeMapper.INSTANCE;
    @InjectMocks
    EmployeeService employeeService;
    @Mock
    private EmployeeRepository repository;

    @Test
    void whenEmployeeListIsCalledThenReturnAListOfEmployees() {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(employeeDTO);

        //when
        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(employee));

        //then
        List<EmployeeDTO> foundEmployeeDTOList = employeeService.findAll();
        MatcherAssert.assertThat(foundEmployeeDTOList, Matchers.is(Matchers.not(Matchers.empty())));
        MatcherAssert.assertThat(foundEmployeeDTOList.get(0), Matchers.is(Matchers.equalTo(employeeDTO)));


    }


    @Test
    void whenEmployeeListIsCalledThenReturnAnEmptyListOfEmployees() {

        //given


        //when
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        //then
        List<EmployeeDTO> foundEmployeeDTOList = employeeService.findAll();
        MatcherAssert.assertThat(foundEmployeeDTOList, Matchers.is(Matchers.empty()));


    }


    @Test
    void whenAnEmployeeIsInformedThenItShouldBeCreated() throws EmployeeAlreadyExistsException, SalaryIncompatibleWithRoleException, ProfitShareExceedsMaxException {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);

        //when
        Mockito.when(repository.findByCPF(employee.getCPF())).thenReturn(Optional.empty());
        Mockito.when(repository.save(employee)).thenReturn(employee);

        //then
        EmployeeDTO createdEmployeeDTO = employeeService.createEmployee(expectedEmployeeDTO);
        MatcherAssert.assertThat(createdEmployeeDTO, Matchers.is(Matchers.equalTo(expectedEmployeeDTO)));

    }


    @Test
    void whenCreateEmployeeIsCalledWithAnAlreadyRegisteredEmployeeThenAnExceptionShouldBeThrow() {

        //given
        EmployeeDTO employeeAlreadyRegisteredDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(employeeAlreadyRegisteredDTO);

        //when
        Mockito.when(repository.findByCPF(employeeAlreadyRegisteredDTO.getCPF())).thenReturn(Optional.of(employee));

        //then
        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.createEmployee(employeeAlreadyRegisteredDTO));


    }


    @Test
    void whenCreateEmployeeIsCalledWithSalaryIncompatibleWithRoleThenAnExceptionShouldBeThrown() {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setSalary(0);
        employeeDTO.setRole(Role.OWNER);

        //when
        Mockito.when(repository.findByCPF(employeeDTO.getCPF())).thenReturn(Optional.empty());


        //then
        assertThrows(SalaryIncompatibleWithRoleException.class,
                () -> employeeService.createEmployee(employeeDTO));


    }


    @Test
    void whenCreateEmployeeIsCalledWithProfitShareExceedingMaxThenAnExceptionShouldBeThrown() {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when
        Mockito.when(repository.findByCPF(employeeDTO.getCPF())).thenReturn(Optional.empty());


        //then
        employeeDTO.setProfitShare(100);
        employeeDTO.setMaxProfitShare(10);
        assertThrows(ProfitShareExceedsMaxException.class,
                () -> employeeService.createEmployee(employeeDTO));


    }


    @Test
    void whenFindByCpfIsCalledWithAValidEmployeesCpfThenReturnAnEmployee() throws EmployeeNotFoundException {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee expectedEmployee = employeeMapper.toModel(employeeDTO);

        //when

        Mockito.when(repository.findByCPF(expectedEmployee.getCPF())).thenReturn(Optional.of(expectedEmployee));

        //then
        EmployeeDTO foundEmployeeDTO = employeeService.findByCPF(employeeDTO.getCPF());
        MatcherAssert.assertThat(foundEmployeeDTO, Matchers.is(Matchers.equalTo(employeeDTO)));
    }

    @Test
    void whenFindByCpfIsCalledWithANotRegisteredEmployeesCpfIsGivenThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findByCPF(expectedEmployeeDTO.getCPF())).thenReturn(Optional.empty());

        //then
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findByCPF(expectedEmployeeDTO.getCPF()));


    }

    @Test
    void whenFireEmployeeIsCalledWithAValidIdThenAnEmployeeShouldBeFired() throws EmployeeNotFoundException {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(employeeDTO);

        //when
        Mockito.when(repository.findById(employeeDTO.getId())).thenReturn(Optional.of(employee));


        //then
        employeeService.fireEmployee(employee.getId());
        LocalDate dateExpected = employee.getDismissalDate();

        MatcherAssert.assertThat(LocalDate.now(), Matchers.is(Matchers.equalTo(dateExpected)));

    }


    @Test
    void whenFireEmployeeIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when
        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.fireEmployee(expectedEmployeeDTO.getId()));


    }


    @Test
    void whenExclusionIsCalledWithAValidIdThenAnEmployeeShouldBeDeleted() throws EmployeeNotFoundException {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(employeeDTO);

        //when
        Mockito.when(repository.findById(employeeDTO.getId())).thenReturn(Optional.of(employee));
        Mockito.doNothing().when(repository).deleteById(employeeDTO.getId());

        //then
        employeeService.deleteEmployee(employeeDTO.getId());
        Mockito.verify(repository, Mockito.times(1)).findById(employeeDTO.getId());
        Mockito.verify(repository, Mockito.times(1)).deleteById(employeeDTO.getId());


    }

    @Test
    void whenExclusionIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(expectedEmployeeDTO.getId()));


    }


    @Test
    void whenRaiseSalaryIsCalledThenItShouldBeRaised() throws EmployeeNotFoundException {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when
        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));
        Mockito.when(repository.save(employee)).thenReturn(employee);

        //then
        double value = 100;
        expectedEmployeeDTO.setSalary(expectedEmployeeDTO.getSalary() + value);
        EmployeeDTO createdEmployeeDTO = employeeService.raiseSalary(expectedEmployeeDTO.getId(), value);
        MatcherAssert.assertThat(createdEmployeeDTO.getSalary(), Matchers.is(Matchers.equalTo(expectedEmployeeDTO.getSalary())));

    }


    @Test
    void whenRaiseSalaryIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        double value = 100;
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.raiseSalary(expectedEmployeeDTO.getId(), value));


    }


    @Test
    void whenRaiseProfitShareIsCalledThenItShouldBeRaised() throws EmployeeNotFoundException, ProfitShareExceedsMaxException {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when
        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));
        Mockito.when(repository.save(employee)).thenReturn(employee);

        //then
        double value = 10;
        expectedEmployeeDTO.setProfitShare(expectedEmployeeDTO.getProfitShare() + value);
        EmployeeDTO createdEmployeeDTO = employeeService.raiseProfitShare(expectedEmployeeDTO.getId(), value);
        MatcherAssert.assertThat(createdEmployeeDTO.getProfitShare(), Matchers.is(Matchers.equalTo(expectedEmployeeDTO.getProfitShare())));

    }


    @Test
    void whenRaiseProfitShareIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        double value = 10;
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.raiseProfitShare(expectedEmployeeDTO.getId(), value));


    }

    @Test
    void whenRaiseProfitShareIsCalledWithAValueGreaterThanMaxThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when

        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));


        //then
        double value = expectedEmployeeDTO.getMaxProfitShare() + 1;
        assertThrows(ProfitShareExceedsMaxException.class, () -> employeeService.raiseProfitShare(expectedEmployeeDTO.getId(), value));


    }

    @Test
    void whenRaiseProfitShareIsCalledWithAValueAfterSumGreaterThanMaxThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when

        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));


        //then
        double value = expectedEmployeeDTO.getMaxProfitShare() - 1;
        assertThrows(ProfitShareExceedsMaxException.class, () -> employeeService.raiseProfitShare(expectedEmployeeDTO.getId(), value));


    }

    @Test
    void whenLowerProfitShareIsCalledThenItShouldBeLowered() throws EmployeeNotFoundException, ProfitShareLesserThanZeroException {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when
        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));
        Mockito.when(repository.save(employee)).thenReturn(employee);

        //then

        double value = 10;
        expectedEmployeeDTO.setProfitShare(expectedEmployeeDTO.getProfitShare() - value);
        EmployeeDTO createdEmployeeDTO = employeeService.lowerProfitShare(expectedEmployeeDTO.getId(), value);
        MatcherAssert.assertThat(createdEmployeeDTO.getProfitShare(), Matchers.is(Matchers.equalTo(expectedEmployeeDTO.getProfitShare())));

    }


    @Test
    void whenLowerProfitShareIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        double value = 10;
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.lowerProfitShare(expectedEmployeeDTO.getId(), value));


    }

    @Test
    void whenLowerProfitShareIsCalledWithAValueGreaterThanActualProfitShareThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);

        //when

        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));


        //then
        double value = expectedEmployeeDTO.getProfitShare() + 1;
        assertThrows(ProfitShareLesserThanZeroException.class, () -> employeeService.lowerProfitShare(expectedEmployeeDTO.getId(), value));


    }

    //....

    @Test
    void whenChangeRoleIsCalledThenItShouldBeChanged() throws EmployeeNotFoundException, SalaryIncompatibleWithRoleException {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(expectedEmployeeDTO);


        //when
        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));
        Mockito.when(repository.save(employee)).thenReturn(employee);

        //then


        expectedEmployeeDTO.setRole(Role.TECHNICIAN);
        EmployeeDTO createdEmployeeDTO = employeeService.changeRole(expectedEmployeeDTO.getId(), Role.TECHNICIAN);
        MatcherAssert.assertThat(createdEmployeeDTO.getRole(), Matchers.is(Matchers.equalTo(expectedEmployeeDTO.getRole())));

    }

    @Test
    void whenChangeRoleIsCalledWithANotRegisteredIdThenThrowsAnException() {

        //given
        EmployeeDTO expectedEmployeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();


        //when

        Mockito.when(repository.findById(expectedEmployeeDTO.getId())).thenReturn(Optional.empty());

        //then
        Role value = Role.OWNER;
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.changeRole(expectedEmployeeDTO.getId(), value));


    }

    @Test
    void whenChangeRoleIsCalledWithSalaryLesserThanMinimalThenAnExceptionShouldBeThrown() {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        Employee employee = employeeMapper.toModel(employeeDTO);
        employee.setSalary(10);


        //when
        Mockito.when(repository.findById(employee.getId())).thenReturn(Optional.of(employee));


        //then
        Role value = Role.MANAGER;
        assertThrows(SalaryIncompatibleWithRoleException.class, () -> employeeService.changeRole(employeeDTO.getId(), value));


    }


}
