package com.tu.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.service.EmployeeService;

// same setup as DepartmentControllerTest; these tests add the cases that are new for employees
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void createReturns201AndTheSavedEmployee() throws Exception {
        EmployeeDto saved = new EmployeeDto();
        saved.setId(1L);
        saved.setEmail("john.doe@example.com");
        when(employeeService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john.doe@example.com",
                                  "phoneNumber": "+12025550101",
                                  "departmentId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createReportsEveryInvalidFieldAtOnce() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "J",
                                  "lastName": "D",
                                  "email": "not-an-email",
                                  "phoneNumber": "123",
                                  "profilePicture": "ftp://example.com/john.png"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").value("First name must be between 2 and 15 characters"))
                .andExpect(jsonPath("$.errors.lastName").value("Last name must be between 2 and 20 characters"))
                .andExpect(jsonPath("$.errors.email").value("Email should be valid"))
                .andExpect(jsonPath("$.errors.phoneNumber").value("Phone number must be a valid format"))
                .andExpect(jsonPath("$.errors.profilePicture").value("Profile picture must be a valid http or https URL"))
                .andExpect(jsonPath("$.errors.departmentId").value("Department is required"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void getByIdReturns400WhenTheIdIsNotANumber() throws Exception {
        // Spring itself rejects "abc" before the controller runs; because GlobalExceptionHandler extends
        // ResponseEntityExceptionHandler, Spring's own errors come back as ProblemDetail too
        mockMvc.perform(get("/api/employees/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: 'abc'"));

        verifyNoInteractions(employeeService);
    }

}
