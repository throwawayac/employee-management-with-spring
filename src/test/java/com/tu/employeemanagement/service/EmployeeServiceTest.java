package com.tu.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.exception.ConflictException;
import com.tu.employeemanagement.exception.ResourceNotFoundException;
import com.tu.employeemanagement.mapper.EmployeeMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.model.Employee;
import com.tu.employeemanagement.repository.EmployeeRepository;

// same idea as DepartmentServiceTest. DepartmentService is a mock here too, so only EmployeeService's rules run
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getByIdTreatsASoftDeletedEmployeeAsNotFound() {
        Employee deleted = Employee.builder().id(3L).isDeleted(true).build();
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> employeeService.getById(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee 3 not found");
    }

    @Test
    void createLinksTheEmployeeToTheRequestedDepartment() {
        EmployeeDto request = johnDoe();
        Employee employee = new Employee();
        Department finance = Department.builder().id(1L).name("Finance").build();
        when(employeeMapper.toEntity(request)).thenReturn(employee);
        when(departmentService.findDepartment(1L)).thenReturn(finance);

        employeeService.create(request);

        // the mapper ignores the department, so the service has to look it up by departmentId and set it
        assertThat(employee.getDepartment()).isSameAs(finance);
        verify(employeeRepository).save(employee);
    }

    @Test
    void createRejectsAnEmailThatIsInUse() {
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(johnDoe()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Employee email is already in use");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createRejectsAPhoneNumberThatIsInUse() {
        when(employeeRepository.existsByPhoneNumber("+12025550101")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(johnDoe()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Employee phone number is already in use");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createSkipsThePhoneCheckWhenThereIsNoPhoneNumber() {
        EmployeeDto request = johnDoe();
        request.setPhoneNumber(null);
        when(employeeMapper.toEntity(request)).thenReturn(new Employee());

        employeeService.create(request);

        // existsByPhoneNumber(null) would match every employee without a phone number
        verify(employeeRepository, never()).existsByPhoneNumber(any());
    }

    @Test
    void updateAcceptsTheEmployeesOwnEmailAndPhoneNumber() {
        Employee john = Employee.builder().id(1L).email("john.doe@example.com").phoneNumber("+12025550101").build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(john));

        employeeService.update(1L, johnDoe());

        // unchanged values aren't checked, otherwise they would count as duplicates of themselves
        verify(employeeRepository, never()).existsByEmail(any());
        verify(employeeRepository, never()).existsByPhoneNumber(any());
        verify(employeeRepository).save(john);
    }

    @Test
    void updateRejectsAnEmailThatIsInUse() {
        Employee john = Employee.builder().id(1L).email("john.doe@example.com").build();
        EmployeeDto request = johnDoe();
        request.setEmail("jane.smith@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(john));
        when(employeeRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Employee email is already in use");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateRejectsAPhoneNumberThatIsInUse() {
        Employee john = Employee.builder().id(1L).email("john.doe@example.com").phoneNumber("+12025550101").build();
        EmployeeDto request = johnDoe();
        request.setPhoneNumber("+12025550102");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(john));
        when(employeeRepository.existsByPhoneNumber("+12025550102")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Employee phone number is already in use");
        verify(employeeRepository, never()).save(any());
    }

    // a valid request; each test changes only the field it's about
    private static EmployeeDto johnDoe() {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+12025550101");
        dto.setDepartmentId(1L);
        return dto;
    }

}
