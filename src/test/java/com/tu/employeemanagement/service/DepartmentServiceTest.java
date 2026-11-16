package com.tu.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.exception.ConflictException;
import com.tu.employeemanagement.exception.ResourceNotFoundException;
import com.tu.employeemanagement.mapper.DepartmentMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.repository.DepartmentRepository;
import com.tu.employeemanagement.repository.EmployeeRepository;

// a unit test: no Spring and no database, so it runs in milliseconds. Mockito replaces the service's dependencies
// with mocks: fake objects that return what a test tells them to, and false, null or an empty list otherwise
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks // a real DepartmentService, created with the three mocks above
    private DepartmentService departmentService;

    @Test
    void getAllLeavesOutSoftDeletedDepartments() {
        Department finance = Department.builder().id(1L).name("Finance").build();
        Department closed = Department.builder().id(2L).name("Closed").isDeleted(true).build();
        DepartmentDto financeDto = new DepartmentDto();
        when(departmentRepository.findAll()).thenReturn(List.of(finance, closed));
        when(departmentMapper.toDto(finance)).thenReturn(financeDto);

        List<DepartmentDto> result = departmentService.getAll();

        assertThat(result).containsExactly(financeDto);
    }

    @Test
    void getByIdReturnsTheMappedDepartment() {
        Department finance = Department.builder().id(1L).name("Finance").build();
        DepartmentDto financeDto = new DepartmentDto();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(finance));
        when(departmentMapper.toDto(finance)).thenReturn(financeDto);

        DepartmentDto result = departmentService.getById(1L);

        assertThat(result).isSameAs(financeDto); // exactly what the mapper made from the department that was found
    }

    @Test
    void getByIdThrowsNotFoundForAMissingDepartment() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department 99 not found");
    }

    @Test
    void getByIdTreatsASoftDeletedDepartmentAsNotFound() {
        Department closed = Department.builder().id(2L).name("Closed").isDeleted(true).build();
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(closed));

        assertThatThrownBy(() -> departmentService.getById(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department 2 not found");
    }

    @Test
    void getWithoutEmployeesLeavesOutSoftDeletedDepartments() {
        // the query itself is tested against a database in DepartmentRepositoryTest; here it's a mock
        Department research = Department.builder().id(5L).name("Research").build();
        Department closed = Department.builder().id(2L).name("Closed").isDeleted(true).build();
        DepartmentDto researchDto = new DepartmentDto();
        when(departmentRepository.findDepartmentsWithoutEmployees()).thenReturn(List.of(research, closed));
        when(departmentMapper.toDto(research)).thenReturn(researchDto);

        List<DepartmentDto> result = departmentService.getWithoutEmployees();

        assertThat(result).containsExactly(researchDto);
    }

    @Test
    void createSavesTheDepartmentWithoutTheIdSentByTheClient() {
        DepartmentDto request = departmentDto("Research");
        request.setId(7L);
        Department research = Department.builder().id(7L).name("Research").build();
        DepartmentDto response = new DepartmentDto();
        when(departmentMapper.toEntity(request)).thenReturn(research);
        when(departmentRepository.save(research)).thenReturn(research);
        when(departmentMapper.toDto(research)).thenReturn(response);

        DepartmentDto result = departmentService.create(request);

        assertThat(result).isSameAs(response);
        assertThat(research.getId()).isNull(); // cleared before saving, so the database generates the id
    }

    @Test
    void createRejectsANameThatIsInUse() {
        when(departmentRepository.existsByName("Finance")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(departmentDto("Finance")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Department name is already in use");
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateAcceptsTheDepartmentsOwnName() {
        Department finance = Department.builder().id(1L).name("Finance").build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(finance));

        departmentService.update(1L, departmentDto("Finance"));

        verify(departmentRepository, never()).existsByName(any()); // an unchanged name isn't checked for duplicates
        verify(departmentRepository).save(finance);
    }

    @Test
    void deleteRejectsADepartmentThatStillHasEmployees() {
        Department finance = Department.builder().id(1L).name("Finance").build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(finance));
        when(employeeRepository.existsByDepartmentIdAndIsDeletedFalse(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Department still has employees");
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteOnlyMarksTheDepartmentAsDeleted() {
        Department finance = Department.builder().id(1L).name("Finance").build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(finance));

        departmentService.delete(1L);

        assertThat(finance.isDeleted()).isTrue(); // a soft delete: the row stays, marked as deleted
        verify(departmentRepository).save(finance);
        verify(departmentRepository, never()).delete(any());
    }

    private static DepartmentDto departmentDto(String name) {
        DepartmentDto dto = new DepartmentDto();
        dto.setName(name);
        return dto;
    }

}
