package com.learning.employee_management.controller;


import com.learning.employee_management.builder.EmployeeChangeableValuesBuilder;
import com.learning.employee_management.builder.EmployeeDTOBuilder;
import com.learning.employee_management.employeeDTO.EmployeeChangeableValues;
import com.learning.employee_management.employeeDTO.EmployeeDTO;
import com.learning.employee_management.enums.Role;
import com.learning.employee_management.exception.EmployeeNotFoundException;
import com.learning.employee_management.exception.ProfitShareExceedsMaxException;
import com.learning.employee_management.exception.ProfitShareLesserThanZeroException;
import com.learning.employee_management.exception.SalaryIncompatibleWithRoleException;
import com.learning.employee_management.services.EmployeeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.learning.employee_management.utils.JsonConvertionUtils.objectToJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private static final String EMPLOYEE_URL_PATH = "/api/v1/employee";
    private static final String FIRE_EMPLOYEE_URL_PATH = "/fireEmployee";
    private static final String RAISE_SALARY_URL_PATH = "/raiseSalary";
    private static final String RAISE_PROFIT_SHARE_URL_PATH = "/raiseProfitShare";
    private static final String LOWER_PROFIT_SHARE_URL_PATH = "/lowerProfitShare";
    private static final String CHANGE_ROLE_URL_PATH = "/changeRole";

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((a, locale) -> new MappingJackson2JsonView()).build();
    }


    @Test
    void whenGETListWithEmployeesIsCalledThenAnOKStatusIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.findAll()).thenReturn(Collections.singletonList(employeeDTO));

        //then

        mockMvc.perform(MockMvcRequestBuilders.get(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", Matchers.is(employeeDTO.getName())))
                .andExpect(jsonPath("$[0].cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$[0].admissionDate", Matchers.is(employeeDTO.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))))
                .andExpect(jsonPath("$[0].dismissalDate", Matchers.is(employeeDTO.getDismissalDate())))
                .andExpect(jsonPath("$[0].salary", Matchers.is(employeeDTO.getSalary())))
                .andExpect(jsonPath("$[0].role", Matchers.is(employeeDTO.getRole().toString())));


    }


    @Test
    void whenPOSTIsCalledThenAnEmployeeIsCreated() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.createEmployee(employeeDTO)).thenReturn(employeeDTO);

        //then
        mockMvc.perform(post(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(employeeDTO.getName())))
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.admissionDate", Matchers.is(employeeDTO.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))))
                .andExpect(jsonPath("$.dismissalDate", Matchers.is(employeeDTO.getDismissalDate())))
                .andExpect(jsonPath("$.salary", Matchers.is(employeeDTO.getSalary())))
                .andExpect(jsonPath("$.role", Matchers.is(employeeDTO.getRole().toString())));

    }

    @Test
    void whenPOSTIsCalledWithoutARequiredFieldAnErrorIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setName(null);


        //then
        mockMvc.perform(post(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenPOSTIsCalledWithAFieldWithInvalidValueAnErrorIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setSalary(500);
        employeeDTO.setCPF("33abcd");
        employeeDTO.setName("a");
        employeeDTO.setLastName("afssdfsdfsfsfsffsfsffssffsfssfsfd");


        //then
        mockMvc.perform(post(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeDTO)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void whenPOSTIsCalledWithASalaryIncompatibleWithRoleABadRequestStatusIsReturned() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setSalary(2000);
        employeeDTO.setRole(Role.DIRECTOR);


        //when
        Mockito.when(employeeService.createEmployee(employeeDTO))
                .thenThrow(SalaryIncompatibleWithRoleException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeDTO)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void whenPOSTIsCalledWithAValueGreaterThanMaxABadRequestStatusIsReturned() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setProfitShare(employeeDTO.getMaxProfitShare() + 1);

        //when
        Mockito.when(employeeService.createEmployee(employeeDTO))
                .thenThrow(ProfitShareExceedsMaxException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeDTO)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void whenGETIsCalledWithAValidCpfThenAnOKStatusIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.findByCPF(employeeDTO.getCPF())).thenReturn(employeeDTO);

        //then

        mockMvc.perform(MockMvcRequestBuilders.get(EMPLOYEE_URL_PATH + "/" + employeeDTO.getCPF())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(employeeDTO.getName())))
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.admissionDate", Matchers.is(employeeDTO.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))))
                .andExpect(jsonPath("$.dismissalDate", Matchers.is(employeeDTO.getDismissalDate())))
                .andExpect(jsonPath("$.salary", Matchers.is(employeeDTO.getSalary())))
                .andExpect(jsonPath("$.role", Matchers.is(employeeDTO.getRole().toString())));


    }


    @Test
    void whenGETIsCalledWithoutARegisteredCpfThenNotFoundStatusIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.findByCPF(employeeDTO.getCPF())).thenThrow(EmployeeNotFoundException.class);

        //then

        mockMvc.perform(MockMvcRequestBuilders.get(EMPLOYEE_URL_PATH + "/" + employeeDTO.getCPF())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


    }


    @Test
    void whenPATCHIsCalledToFireEmployeeThenOkStatusIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.fireEmployee(employeeDTO.getId())).thenReturn(employeeDTO);

        //then

        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + FIRE_EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.admissionDate", Matchers.is(employeeDTO.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))))
                .andExpect(jsonPath("$.dismissalDate", Matchers.is(employeeDTO.getDismissalDate())));


    }

    @Test
    void whenPATCHIsCalledToFireEmployeeWithoutARegisteredIdThenNotFoundStatusIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.when(employeeService.fireEmployee(employeeDTO.getId())).thenThrow(EmployeeNotFoundException.class);

        //then

        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + FIRE_EMPLOYEE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


    }


    @Test
    void whenDeleteIsCalledWithAValidIdThenNoContentIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.doNothing().when(employeeService).deleteEmployee(employeeDTO.getId());

        //then

        mockMvc.perform(MockMvcRequestBuilders.delete(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


    }

    @Test
    void whenDeleteIsCalledWithAnInValidIdThenNotFoundIsReturned() throws Exception {

        //given
        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        //when
        Mockito.doThrow(EmployeeNotFoundException.class).when(employeeService).deleteEmployee(employeeDTO.getId());

        //then

        mockMvc.perform(MockMvcRequestBuilders.delete(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


    }


    @Test
    void whenPATCHRaiseSalaryIsCalledThenSalaryIsRaised() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.raiseSalary(employeeDTO.getId(), employeeChangeableValues.getValue()))
                .thenReturn(employeeDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + RAISE_SALARY_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.salary", Matchers.is(employeeDTO.getSalary())))
                .andExpect(jsonPath("$.role", Matchers.is(employeeDTO.getRole().toString())));

    }


    @Test
    void whenPATCHRaiseSalaryIsCalledWithInvalidIdANotFoundStatusIsReturned() throws Exception {

        //given

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.raiseSalary(-1L, employeeChangeableValues.getValue()))
                .thenThrow(EmployeeNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + -1L + RAISE_SALARY_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isNotFound());

    }


    @Test
    void whenPATCHRaiseProfitShareIsCalledThenProfitShareIsRaised() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.raiseProfitShare(employeeDTO.getId(), employeeChangeableValues.getValue()))
                .thenReturn(employeeDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + RAISE_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.profitShare", Matchers.is(employeeDTO.getProfitShare())))
                .andExpect(jsonPath("$.maxProfitShare", Matchers.is(employeeDTO.getMaxProfitShare())));

    }


    @Test
    void whenPATCHRaiseProfitShareIsCalledWithInvalidIdANotFoundStatusIsReturned() throws Exception {

        //given

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.raiseProfitShare(-1L, employeeChangeableValues.getValue()))
                .thenThrow(EmployeeNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + -1L + RAISE_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isNotFound());

    }


    @Test
    void whenPATCHRaiseProfitShareIsCalledWithAValueGreaterThanMaxABadRequestStatusIsReturned() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        employeeChangeableValues.setValue(employeeDTO.getMaxProfitShare() + 1);

        //when
        Mockito.when(employeeService.raiseProfitShare(employeeDTO.getId(), employeeChangeableValues.getValue()))
                .thenThrow(ProfitShareExceedsMaxException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + RAISE_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void whenPATCHLowerProfitShareIsCalledThenProfitShareIsLowered() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.lowerProfitShare(employeeDTO.getId(), employeeChangeableValues.getValue()))
                .thenReturn(employeeDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + LOWER_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.profitShare", Matchers.is(employeeDTO.getProfitShare())))
                .andExpect(jsonPath("$.maxProfitShare", Matchers.is(employeeDTO.getMaxProfitShare())));

    }


    @Test
    void whenPATCHLowerProfitShareIsCalledWithInvalidIdANotFoundStatusIsReturned() throws Exception {

        //given

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.lowerProfitShare(-1L, employeeChangeableValues.getValue()))
                .thenThrow(EmployeeNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + -1L + LOWER_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isNotFound());

    }


    @Test
    void whenPATCHLowerProfitShareIsCalledWithAResultValueLesserThanZeroABadRequestStatusIsReturned() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        employeeChangeableValues.setValue(employeeDTO.getProfitShare() + 1);

        //when
        Mockito.when(employeeService.lowerProfitShare(employeeDTO.getId(), employeeChangeableValues.getValue()))
                .thenThrow(ProfitShareLesserThanZeroException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + LOWER_PROFIT_SHARE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void whenPATCHChangeRoleIsCalledThenRoleIsChanged() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setRole(Role.MANAGER);

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();
        employeeChangeableValues.setRole(Role.TECHNICIAN);

        //when
        Mockito.when(employeeService.changeRole(employeeDTO.getId(), employeeChangeableValues.getRole()))
                .thenReturn(employeeDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + CHANGE_ROLE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", Matchers.is(employeeDTO.getCPF())))
                .andExpect(jsonPath("$.salary", Matchers.is(employeeDTO.getSalary())))
                .andExpect(jsonPath("$.role", Matchers.is(employeeDTO.getRole().toString())));

    }


    @Test
    void whenPATCHChangeRoleIsCalledWithInvalidIdANotFoundStatusIsReturned() throws Exception {

        //given
        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();

        //when
        Mockito.when(employeeService.changeRole(-1L, employeeChangeableValues.getRole()))
                .thenThrow(EmployeeNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + -1L + CHANGE_ROLE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isNotFound());

    }


    @Test
    void whenPATCHChangeRoleIsCalledWithASalaryIncompatibleWithRoleABadRequestStatusIsReturned() throws Exception {

        //given

        EmployeeDTO employeeDTO = EmployeeDTOBuilder.builder().build().toEmployeeDTO();
        employeeDTO.setSalary(2000);

        EmployeeChangeableValues employeeChangeableValues = EmployeeChangeableValuesBuilder.builder()
                .build().toEmployeeChangeableValues();
        employeeChangeableValues.setRole(Role.DIRECTOR);


        //when
        Mockito.when(employeeService.changeRole(employeeDTO.getId(), employeeChangeableValues.getRole()))
                .thenThrow(SalaryIncompatibleWithRoleException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(EMPLOYEE_URL_PATH + "/" + employeeDTO.getId() + CHANGE_ROLE_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(employeeChangeableValues)))
                .andExpect(status().isBadRequest());

    }


}
