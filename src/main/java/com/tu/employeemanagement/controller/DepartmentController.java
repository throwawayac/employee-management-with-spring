package com.tu.employeemanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.mapper.DepartmentMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.repository.DepartmentRepository;
import com.tu.employeemanagement.repository.EmployeeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    @GetMapping // GET /api/departments
    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .filter(department -> !department.isDeleted()) // soft-deleted departments are hidden
                .map(departmentMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}") // GET /api/departments/1
    public DepartmentDto getById(@PathVariable Long id) {
        return departmentMapper.toDto(findDepartment(id));
    }

    @GetMapping("/without-employees") // GET /api/departments/without-employees (the LEFT JOIN @Query)
    public List<DepartmentDto> getWithoutEmployees() {
        return departmentRepository.findDepartmentsWithoutEmployees().stream()
                .filter(department -> !department.isDeleted())
                .map(departmentMapper::toDto)
                .toList();
    }

    @PostMapping // POST /api/departments
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@Valid @RequestBody DepartmentDto dto) {
        // also counts soft-deleted departments: their names are still in the table's UNIQUE column
        if (departmentRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already in use");
        }

        Department department = departmentMapper.toEntity(dto);
        department.setId(null); // the database generates the id, ignore one sent by the client
        return departmentMapper.toDto(departmentRepository.save(department));
    }

    @PutMapping("/{id}") // PUT /api/departments/1
    public DepartmentDto update(@PathVariable Long id, @Valid @RequestBody DepartmentDto dto) {
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

    @DeleteMapping("/{id}") // DELETE /api/departments/1 (soft delete: the row stays, marked as deleted)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Department department = findDepartment(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department still has employees");
        }

        department.setDeleted(true);
        departmentRepository.save(department);
    }

    @Deprecated // use the soft delete above; this removes the row for good
    @DeleteMapping("/hard-delete/{id}") // DELETE /api/departments/hard-delete/1
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable Long id) {
        Department department = departmentRepository.findById(id) // also finds soft-deleted departments
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department " + id + " not found"));
        // orphanRemoval = true would delete the department's employees along with it, so refuse instead
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department still has employees");
        }
        
        departmentRepository.delete(department);
    }

    // soft-deleted departments count as not found
    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .filter(department -> !department.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department " + id + " not found"));
    }

}
