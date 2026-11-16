package com.tu.employeemanagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.exception.ConflictException;
import com.tu.employeemanagement.exception.ResourceNotFoundException;
import com.tu.employeemanagement.service.DepartmentService;

// a slice test: Spring starts only the web layer (this controller, GlobalExceptionHandler, JSON and validation)
// and MockMvc sends it requests without a real server. The service is a mock, so no database is involved:
// these tests check the HTTP side, i.e. URLs, status codes, JSON bodies and error responses
@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // a Mockito mock that Spring injects into the controller instead of the real service
    private DepartmentService departmentService;

    @Test
    void getAllReturnsTheDepartmentsAsJson() throws Exception {
        when(departmentService.getAll()).thenReturn(List.of(department(1L, "Finance"), department(2L, "Marketing")));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Finance"))
                .andExpect(jsonPath("$[1].name").value("Marketing"));
    }

    @Test
    void createReturns201AndTheSavedDepartment() throws Exception {
        // any(): the controller creates its own DepartmentDto from the JSON, so the test can't pass that object
        when(departmentService.create(any())).thenReturn(department(5L, "Research"));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Research" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Research"));
    }

    @Test
    void createRejectsATooShortName() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "HR" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Department name must be between 3 and 30 characters"));

        verifyNoInteractions(departmentService); // @Valid rejected the request before the controller method ran
    }

    @Test
    void createReportsEveryRuleABlankNameBreaks() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "" }
                                """))
                .andExpect(status().isBadRequest())
                // the two messages are joined with "; " in no fixed order, so check for each one separately
                .andExpect(jsonPath("$.errors.name").value(containsString("Department name is required")))
                .andExpect(jsonPath("$.errors.name")
                        .value(containsString("Department name must be between 3 and 30 characters")));
    }

    @Test
    void getByIdReturns404ForAMissingDepartment() throws Exception {
        when(departmentService.getById(99L)).thenThrow(new ResourceNotFoundException("Department 99 not found"));

        mockMvc.perform(get("/api/departments/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Department 99 not found"));
    }

    @Test
    void deleteReturns409WhenTheDepartmentStillHasEmployees() throws Exception {
        // delete() returns void, so the exception is set up with doThrow(...).when(mock) instead of when(...)
        doThrow(new ConflictException("Department still has employees")).when(departmentService).delete(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Department still has employees"));
    }

    @Test
    void deleteReturns204AndPassesTheIdToTheService() throws Exception {
        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent());

        verify(departmentService).delete(1L);
    }

    private static DepartmentDto department(Long id, String name) {
        return new DepartmentDto(id, name, Set.of(), false);
    }

}
