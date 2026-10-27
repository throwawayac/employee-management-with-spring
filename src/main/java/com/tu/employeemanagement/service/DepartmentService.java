package com.tu.employeemanagement.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.mapper.DepartmentMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.repository.DepartmentRepository;
import com.tu.employeemanagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional // each public method runs in one transaction: its checks and its save succeed or fail together
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .filter(department -> !department.isDeleted()) // soft-deleted departments are hidden
                .map(departmentMapper::toDto)
                .toList();
    }

    public DepartmentDto getById(Long id) {
        return departmentMapper.toDto(findDepartment(id));
    }

    public List<DepartmentDto> getWithoutEmployees() {
        return departmentRepository.findDepartmentsWithoutEmployees().stream() // the LEFT JOIN @Query
                .filter(department -> !department.isDeleted())
                .map(departmentMapper::toDto)
                .toList();
    }

    public DepartmentDto create(DepartmentDto dto) {
        // also counts soft-deleted departments: their names are still in the table's UNIQUE column
        if (departmentRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already in use");
        }

        Department department = departmentMapper.toEntity(dto);
        department.setId(null); // the database generates the id, ignore one sent by the client
        return departmentMapper.toDto(departmentRepository.save(department));
    }

    public DepartmentDto update(Long id, DepartmentDto dto) {
        Department department = findDepartment(id);
        // only check a changed name, otherwise the department's own name would count as a duplicate
        if (!dto.getName().equals(department.getName()) && departmentRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already in use");
        }

        // change the loaded department instead of saving a new one built by the mapper: that one would have an
        // empty employees set, and with orphanRemoval = true saving it would delete the department's employees
        department.setName(dto.getName());
        return departmentMapper.toDto(departmentRepository.save(department));
    }

    public void delete(Long id) {
        Department department = findDepartment(id);
        // soft-deleted employees don't count, so a department whose employees are all soft-deleted can be deleted
        if (employeeRepository.existsByDepartmentIdAndIsDeletedFalse(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department still has employees");
        }

        department.setDeleted(true);
        departmentRepository.save(department);
    }

    @Deprecated // use delete(); this removes the row for good
    public void hardDelete(Long id) {
        Department department = departmentRepository.findById(id) // also finds soft-deleted departments
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department " + id + " not found"));
        // orphanRemoval = true would delete the department's employees along with it, so refuse instead
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department still has employees");
        }

        departmentRepository.delete(department);
    }

    // soft-deleted departments count as not found; public so EmployeeService uses the same rule
    // (controllers should keep using the methods above, which return DTOs instead of entities)
    public Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .filter(department -> !department.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department " + id + " not found"));
    }

}
